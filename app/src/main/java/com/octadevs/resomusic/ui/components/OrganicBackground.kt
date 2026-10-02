package com.octadevs.resomusic.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.octadevs.resomusic.ui.theme.AmbientWash
import com.octadevs.resomusic.ui.theme.ContourFar
import com.octadevs.resomusic.ui.theme.ContourMid
import com.octadevs.resomusic.ui.theme.ContourNear
import com.octadevs.resomusic.ui.theme.EmberOrange
import com.octadevs.resomusic.ui.theme.NoirAbyss
import kotlin.math.PI
import kotlin.math.sin

/* ============================================================
   ORGANIC BACKDROP
   ------------------------------------------------------------
   The background is not a gradient and not drifting blobs — it is
   a monochrome near-black field carrying faint topographic
   contour banding, which is what the reference design actually
   shows. Sampling it gave #000000 at ~47% of pixels, with the
   brightest banding around #2E2E2E and the bulk of it between
   #1A1A1A and #101010.

   This reproduces that with generated sine-composite contour
   lines rather than a stock image, so it scales to any screen
   without adding a dependency.

   PERFORMANCE
   This used to be the most expensive thing in the app by a wide
   margin. The contour Paths were memoised, which stopped them
   being *rebuilt* — but it did nothing about *re-rasterising*
   them. Every frame of a 68-second and a 97-second infinite
   animation, on every screen, forever, the draw phase re-issued
   44 stroked paths of ~200 segments each, plus two radial
   gradients each spanning more than the full screen width. That
   is roughly 9,000 stroked segments and two full-screen fragment
   shaders per frame, to produce drift of about two pixels per
   second.

   So the field is now rasterised *once per size* into a bitmap,
   and the drift becomes a translation of that bitmap: two
   textured blits per frame instead of thousands of path
   operations. Half resolution, because the lines are 1-1.5dp at
   very low contrast — the slight softening is invisible, and it
   quarters both the raster cost and the memory. Runtime strength
   is applied through the draw alpha rather than baked in, so
   moving a slider costs nothing.
   ============================================================ */

private const val CONTOUR_BANDS = 22
private const val TWO_PI = (PI * 2.0).toFloat()

/** How far past each edge the field is generated, leaving room to drift. */
private const val FIELD_OVERSCAN = 0.20f

/** Rasterise contours at half resolution and let the GPU scale them up. */
private const val RASTER_SCALE = 0.5f

private data class ContourField(
    val paths: List<Path>,
    /** Every 4th line is an "index contour" — thicker, as on a real map. */
    val isMajor: List<Boolean>
)

private fun buildContourField(size: IntSize): ContourField {
    val w = size.width.toFloat()
    val h = size.height.toFloat()
    if (w <= 0f || h <= 0f) return ContourField(emptyList(), emptyList())

    val startX = -w * FIELD_OVERSCAN
    val spanX = w * (1f + FIELD_OVERSCAN * 2f)
    // ~14dp steps read as smooth curves without flooding the path buffer.
    val step = (spanX / 160f).coerceAtLeast(4f)

    val paths = ArrayList<Path>(CONTOUR_BANDS)
    val major = ArrayList<Boolean>(CONTOUR_BANDS)

    for (i in 0 until CONTOUR_BANDS) {
        val u = i / CONTOUR_BANDS.toFloat()

        // Non-linear spacing makes lines gather into groups instead of
        // marching evenly — that grouping is what reads as "topographic".
        val t = (u + 0.055f * sin(u * TWO_PI * 2.4f)).coerceIn(0.02f, 0.98f)
        val baseY = h * t

        // Swell peaks toward the middle so the field reads as a slow swell
        // rather than as corrugated metal.
        val swell = 0.30f + 0.70f * sin(t * PI).toFloat()
        val amp = h * 0.055f * swell

        val path = Path()
        var first = true
        var x = startX
        while (x <= startX + spanX) {
            val nx = x / w
            val y = baseY
                + amp * sin(nx * TWO_PI * 1.15f + t * 5.10f)
                + amp * 0.60f * sin(nx * TWO_PI * 2.35f - t * 3.30f + 1.70f)
                + amp * 0.33f * sin(nx * TWO_PI * 4.10f + t * 7.70f + 0.40f)
            if (first) {
                path.moveTo(x, y)
                first = false
            } else {
                path.lineTo(x, y)
            }
            x += step
        }
        paths.add(path)
        major.add(i % 4 == 0)
    }
    return ContourField(paths, major)
}

/**
 * Contour brightness falls off away from the vertical centre so the top and
 * bottom of the screen stay near-black — which is exactly where the status
 * bar, the header lockup and the floating dock live. Text never has to fight
 * the pattern.
 */
private fun contourAlpha(t: Float): Float = 0.34f + 0.66f * sin(t * PI).toFloat()

/**
 * Rasterise the whole contour field once, at [scale] of the target size.
 *
 * The field is generated at the *raster* size rather than the screen size, so
 * the paths are already in bitmap coordinates and need no transform. The
 * density handed to the draw scope is scaled to match, which keeps stroke
 * widths in the right proportion without any per-path maths.
 */
private fun rasteriseContours(
    density: Density,
    targetWidth: Float,
    targetHeight: Float,
    scale: Float,
    color: Color
): ImageBitmap? {
    val bw = (targetWidth * scale).toInt()
    val bh = (targetHeight * scale).toInt()
    if (bw <= 0 || bh <= 0) return null

    val field = buildContourField(IntSize(bw, bh))
    if (field.paths.isEmpty()) return null

    val bitmap = try {
        ImageBitmap(bw, bh)
    } catch (_: Throwable) {
        // Allocation failure is survivable: the backdrop simply has no
        // contours. Never let a decorative layer take the screen down.
        return null
    }

    val scope = CanvasDrawScope()
    scope.draw(
        // Half density so 1.5dp strokes land at the right pixel width for a
        // bitmap that will be scaled back up.
        density = Density(density.density * scale),
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(bitmap),
        size = Size(bw.toFloat(), bh.toFloat())
    ) {
        val majorPx = 1.5.dp.toPx()
        val minorPx = 1.0.dp.toPx()
        val n = field.paths.size
        for (i in 0 until n) {
            val t = (i + 0.5f) / n
            val isMajor = field.isMajor[i]
            val alpha = contourAlpha(t) * if (isMajor) 1f else 0.52f
            if (alpha <= 0.004f) continue
            drawPath(
                path = field.paths[i],
                color = color.copy(alpha = alpha),
                style = Stroke(width = if (isMajor) majorPx else minorPx)
            )
        }
    }
    return bitmap
}

/**
 * The app-wide background: a monochrome black field with topographic contour
 * banding and a barely-there ember wash.
 *
 * @param drawBase when false, only the contours are drawn — used over screens
 *   that already paint their own background (e.g. the full player, which lays
 *   blurred artwork underneath).
 */
@Composable
fun OrganicWaveBackdrop(
    modifier: Modifier = Modifier,
    isDark: Boolean = true,
    animate: Boolean = true,
    /** Global contour visibility. 0 hides the pattern entirely. */
    strength: Float = 1f,
    drawBase: Boolean = true,
    /** Accent wash intensity. Kept separate so players can push it harder. */
    wash: Float = 1f,
    washColor: Color = EmberOrange
) {
    val reducedMotion = rememberReducedMotion()
    val shouldAnimate = animate && !reducedMotion

    // One transition driving two values, not two transitions.
    //
    // Each rememberInfiniteTransition is a separate subscription to the frame
    // clock, and each one wakes the composition on every single frame. Two
    // drifts at different rates is what makes the motion read as organic, and
    // one transition can drive both just as well.
    val transition = rememberInfiniteTransition(label = "OrganicDrift")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 68_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrganicDriftValue"
    )
    // Second, slower and counter-directional layer.
    val drift2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 97_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrganicDrift2Value"
    )

    val contourColor = if (isDark) ContourNear else ContourMid
    val farColor = if (isDark) ContourMid else ContourFar
    val baseTop = if (isDark) NoirAbyss else ContourFar

    Box(
        modifier = modifier.drawWithCache {
            val w = size.width
            val h = size.height

            // Built here, not in composition: drawWithCache re-runs this block
            // when the size changes and skips it on every other frame, which is
            // exactly the lifetime a multi-megabyte bitmap wants.
            val contours = if (w > 0f && h > 0f && strength > 0.01f) {
                rasteriseContours(
                    density = this,
                    targetWidth = w,
                    targetHeight = h,
                    scale = RASTER_SCALE,
                    color = contourColor
                )
            } else {
                null
            }

            // Recolours the baked bitmap for the second, dimmer layer while
            // keeping its alpha, so one raster serves both.
            val farFilter = ColorFilter.tint(farColor, BlendMode.SrcIn)

            onDrawBehind {
                if (w <= 0f || h <= 0f) return@onDrawBehind

                if (drawBase) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            // Dead black at the extremes, a touch of lift
                            // through the middle — matches the measured ramp.
                            0f to Color.Black,
                            0.18f to baseTop,
                            0.85f to baseTop,
                            1f to Color.Black
                        ),
                        size = Size(w, h)
                    )

                    // Rare chromatic wash. Two very soft, very slow pools.
                    //
                    // These were previously drawn at 1.05x and 0.95x of the
                    // screen *width* as radius, so each one was shading well
                    // over two screens' worth of pixels every frame. They are
                    // soft enough that a smaller radius is indistinguishable,
                    // and this is roughly half the fragment work.
                    if (wash > 0.01f) {
                        val p1 = if (shouldAnimate) drift else 0.42f
                        val p2 = if (shouldAnimate) drift2 else 0.68f
                        drawCircle(
                            brush = Brush.radialGradient(
                                0f to AmbientWash.copy(alpha = 0.5f * wash),
                                0.45f to AmbientWash.copy(alpha = 0.16f * wash),
                                1f to Color.Transparent
                            ),
                            radius = w * 0.70f,
                            center = Offset(
                                x = w * (0.18f + 0.64f * p1),
                                y = h * (0.16f + 0.22f * sin(p2 * TWO_PI))
                            )
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                0f to washColor.copy(alpha = 0.13f * wash),
                                0.5f to washColor.copy(alpha = 0.05f * wash),
                                1f to Color.Transparent
                            ),
                            radius = w * 0.62f,
                            center = Offset(
                                x = w * (0.86f - 0.58f * p2),
                                y = h * (0.78f - 0.16f * sin(p1 * TWO_PI))
                            )
                        )
                    }
                }

                if (contours != null && strength > 0.01f) {
                    val t1 = if (shouldAnimate) drift else 0.5f
                    val t2 = if (shouldAnimate) drift2 else 0.5f

                    // Two blits, scaled from the half-res raster back up to the
                    // screen. The GPU's bilinear filter does the smoothing for
                    // free, which suits these low-contrast lines.
                    drawImage(
                        image = contours,
                        dstOffset = IntOffset(
                            ((-w * FIELD_OVERSCAN + w * 0.10f * t1).toInt()),
                            (h * 0.012f * sin(t1 * TWO_PI)).toInt()
                        ),
                        dstSize = IntSize(w.toInt(), h.toInt()),
                        alpha = (strength * 0.62f).coerceIn(0f, 1f)
                    )
                    drawImage(
                        image = contours,
                        dstOffset = IntOffset(
                            ((-w * FIELD_OVERSCAN - w * 0.07f * t2).toInt()),
                            (-h * 0.018f * sin(t2 * TWO_PI)).toInt()
                        ),
                        dstSize = IntSize(w.toInt(), h.toInt()),
                        alpha = (strength * 0.34f).coerceIn(0f, 1f),
                        colorFilter = farFilter
                    )
                }
            }
        }
    )
}

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.octadevs.resomusic.ui.theme.AmbientWash
import com.octadevs.resomusic.ui.theme.ContourFar
import com.octadevs.resomusic.ui.theme.ContourMid
import com.octadevs.resomusic.ui.theme.ContourNear
import com.octadevs.resomusic.ui.theme.NoirAbyss
import com.octadevs.resomusic.ui.theme.NoirPurple
import kotlin.math.PI
import kotlin.math.sin

/* ============================================================
   ORGANIC BACKDROP
   ------------------------------------------------------------
   The reference design's background is not a gradient and not
   drifting blobs — it is a monochrome near-black field carrying
   faint topographic contour banding. Sampling it gave #000000 at
   ~47% of pixels, with the brightest banding around #2E2E2E and
   the bulk of it between #1A1A1A and #101010.

   This reproduces that with generated sine-composite contour
   lines rather than a stock image, so it scales cleanly to any
   screen without adding a dependency.

   PERFORMANCE
   The contour geometry is built once per size and then never
   rebuilt. Motion is two counter-drifting copies of that geometry
   translated on the GPU, which is why the background can breathe
   without re-pathing 22 paths every frame.
   ============================================================ */

private const val CONTOUR_BANDS = 22
private const val TWO_PI = (PI * 2.0).toFloat()

/** How far past each edge the field is generated, leaving room to drift. */
private const val FIELD_OVERSCAN = 0.20f

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
    val step = (spanX / 160f).coerceAtLeast(6f)

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

private fun DrawScope.drawContourLayer(
    field: ContourField,
    color: Color,
    strength: Float,
    widthMajor: Dp,
    widthMinor: Dp
) {
    val n = field.paths.size
    if (n == 0) return
    val majorPx = widthMajor.toPx()
    val minorPx = widthMinor.toPx()
    for (i in 0 until n) {
        val t = (i + 0.5f) / n
        val isMajor = field.isMajor[i]
        val alpha = contourAlpha(t) * strength * if (isMajor) 1f else 0.52f
        if (alpha <= 0.004f) continue
        drawPath(
            path = field.paths[i],
            color = color.copy(alpha = alpha),
            style = Stroke(width = if (isMajor) majorPx else minorPx)
        )
    }
}

/**
 * The app-wide background: a monochrome black field with topographic contour
 * banding and a barely-there purple wash.
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
    washColor: Color = NoirPurple
) {
    // Named `fieldSize`, not `size`: inside `drawBehind` the unqualified
    // `size` has to keep resolving to `DrawScope.size` (a Size). A local named
    // `size` shadows it and silently turns every `size.width` into an Int.
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }
    val field = remember(fieldSize) { buildContourField(fieldSize) }

    val reducedMotion = rememberReducedMotion()
    val shouldAnimate = animate && !reducedMotion

    val drift by rememberInfiniteTransition(label = "OrganicDrift").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 68_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrganicDriftValue"
    )
    // Second, slower and counter-directional layer: two drifts at different
    // rates read as organic. One looks like a scrolling texture.
    val drift2 by rememberInfiniteTransition(label = "OrganicDrift2").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 97_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrganicDrift2Value"
    )

    val majorWidth = 1.5.dp
    val minorWidth = 1.0.dp
    val contourColor = if (isDark) ContourNear else ContourMid
    val baseTop = if (isDark) NoirAbyss else ContourFar

    Box(
        modifier = modifier
            .onSizeChanged { fieldSize = it }
            .drawBehind {
                val w = size.width
                val h = size.height
                if (w <= 0f || h <= 0f) return@drawBehind

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
                    if (wash > 0.01f) {
                        val p1 = if (shouldAnimate) drift else 0.42f
                        val p2 = if (shouldAnimate) drift2 else 0.68f
                        drawCircle(
                            brush = Brush.radialGradient(
                                0f to AmbientWash.copy(alpha = 0.5f * wash),
                                0.45f to AmbientWash.copy(alpha = 0.16f * wash),
                                1f to Color.Transparent
                            ),
                            radius = w * 1.05f,
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
                            radius = w * 0.95f,
                            center = Offset(
                                x = w * (0.86f - 0.58f * p2),
                                y = h * (0.78f - 0.16f * sin(p1 * TWO_PI))
                            )
                        )
                    }
                }

                if (strength > 0.01f && field.paths.isNotEmpty()) {
                    val t1 = if (shouldAnimate) drift else 0.5f
                    val t2 = if (shouldAnimate) drift2 else 0.5f

                    withTransform({
                        // Travelling well inside the overscan so no seam shows.
                        translate(
                            left = -w * FIELD_OVERSCAN + w * 0.10f * t1,
                            top = h * 0.012f * sin(t1 * TWO_PI)
                        )
                    }) {
                        drawContourLayer(
                            field = field,
                            color = contourColor,
                            strength = strength * 0.62f,
                            widthMajor = majorWidth,
                            widthMinor = minorWidth
                        )
                    }

                    withTransform({
                        translate(
                            left = -w * FIELD_OVERSCAN - w * 0.07f * t2,
                            top = -h * 0.018f * sin(t2 * TWO_PI)
                        )
                    }) {
                        drawContourLayer(
                            field = field,
                            color = if (isDark) ContourMid else ContourFar,
                            strength = strength * 0.34f,
                            widthMajor = majorWidth,
                            widthMinor = minorWidth
                        )
                    }
                }
            }
    )
}
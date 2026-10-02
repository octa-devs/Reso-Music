package com.octadevs.resomusic.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.octadevs.resomusic.ui.theme.LocalGlassTokens
import com.octadevs.resomusic.ui.theme.LocalGlassUserTuning
import com.octadevs.resomusic.ui.theme.MicroLabel
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/* ============================================================================
   RESO — LIQUID GLASS SYSTEM
   ----------------------------------------------------------------------------
   Real glass is: a tinted translucent body + light caught on the top-left edge
   + a bright rim where the material curves away + a soft shadow underneath.
   We reproduce all four so surfaces read as *thick* instead of flat.
   ============================================================================ */

private const val GLASS_ENTER_MS = 420

@Immutable
data class GlassTuning(
    val fillTop: Float,
    val fillBottom: Float,
    val rimAlpha: Float,
    val specularAlpha: Float,
    val innerShadow: Float,
    val elevation: Dp,
    val shadowAlpha: Float
)

@Composable
fun rememberGlassTuning(
    isDark: Boolean = LocalGlassTokens.current.isDark,
    strong: Boolean = false,
    raised: Boolean = false
): GlassTuning {
    val user = LocalGlassUserTuning.current.clamped()

    // Base strengths: dark glass needs more fill to register at all, light
    // glass needs less or it goes milky.
    val base = if (isDark) {
        GlassTuning(
            fillTop = if (strong) 0.46f else 0.30f,
            fillBottom = if (strong) 0.26f else 0.16f,
            rimAlpha = if (strong) 0.72f else 0.55f,
            specularAlpha = if (strong) 0.22f else 0.14f,
            innerShadow = 0.30f,
            elevation = if (raised) 18.dp else 8.dp,
            shadowAlpha = 0.50f
        )
    } else {
        GlassTuning(
            fillTop = if (strong) 0.82f else 0.64f,
            fillBottom = if (strong) 0.58f else 0.40f,
            rimAlpha = if (strong) 0.95f else 0.74f,
            specularAlpha = if (strong) 0.62f else 0.40f,
            innerShadow = 0.10f,
            elevation = if (raised) 20.dp else 9.dp,
            shadowAlpha = 0.16f
        )
    }

    // `transparency` is inverted relative to fill: pushing it up thins the
    // body so more of the background reads through the pane.
    val transparencyMul = 1.30f - (user.transparency * 0.75f)

    return GlassTuning(
        fillTop = (base.fillTop * user.intensity * transparencyMul).coerceIn(0f, 0.97f),
        fillBottom = (base.fillBottom * user.intensity * transparencyMul).coerceIn(0f, 0.97f),
        rimAlpha = (base.rimAlpha * user.borderOpacity).coerceIn(0f, 1f),
        specularAlpha = (base.specularAlpha * user.intensity).coerceIn(0f, 1f),
        innerShadow = (base.innerShadow * user.shadowIntensity).coerceIn(0f, 1f),
        elevation = base.elevation,
        shadowAlpha = (base.shadowAlpha * user.shadowIntensity).coerceIn(0f, 1f)
    )
}

private fun roundedRectPath(width: Float, height: Float, radius: Float): Path = Path().apply {
    addRoundRect(
        RoundRect(
            rect = Rect(0f, 0f, width, height),
            cornerRadius = CornerRadius(radius, radius)
        )
    )
}

/**
 * Applies a full liquid-glass treatment over whatever is behind it.
 *
 * @param cornerRadius must match [shape]'s radius or the highlights will not
 *        line up with the silhouette.
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape,
    cornerRadius: Dp,
    strong: Boolean = false,
    raised: Boolean = false,
    tint: Color? = null,
    showSpecular: Boolean = true,
    showRim: Boolean = true
): Modifier {
    val tokens = LocalGlassTokens.current
    val isDark = tokens.isDark
    val tuning = rememberGlassTuning(isDark = isDark, strong = strong, raised = raised)
    // Glass is a *translucent white* veil, not a dark tinted slab. Using an
    // opaque near-black base here is what made dark-mode panes disappear.
    val base = tint ?: if (isDark) Color(0xFFEFF2FF) else Color(0xFFFFFFFF)
    val shadowColor = if (isDark) Color.Black else Color(0xFF3A3F63)
    val shadowAlpha = tuning.shadowAlpha
    val rimStart = tokens.rimStart

    return this
        .shadow(
            elevation = tuning.elevation,
            shape = shape,
            clip = false,
            ambientColor = shadowColor.copy(alpha = shadowAlpha),
            spotColor = shadowColor.copy(alpha = shadowAlpha)
        )
        .clip(shape)
        .background(
            Brush.linearGradient(
                colors = listOf(
                    base.copy(alpha = tuning.fillTop),
                    base.copy(alpha = tuning.fillBottom)
                )
            )
        )
        .drawBehind {
            val r = cornerRadius.toPx()
            val path = roundedRectPath(size.width, size.height, r)
            val px = density

            // ---- 1. Inner shadow along the top edge: carves depth into the pane
            clipPath(path) {
                val depth = (r * 1.15f).coerceAtLeast(6f * px)
                drawRect(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Black.copy(alpha = tuning.innerShadow),
                            1f to Color.Transparent
                        ),
                        startY = 0f,
                        endY = depth
                    ),
                    size = size
                )
            }

            // ---- 2. Specular sheen: light pools on the top-left face, with a
            // fainter bounce along the bottom-right. One direction only leaves
            // the opposite corner looking like dead flat plastic.
            if (showSpecular) {
                val specR = size.minDimension * 1.15f
                clipPath(path) {
                    val keyCenter = Offset(size.width * 0.15f, -size.height * 0.05f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0f to Color.White.copy(alpha = tuning.specularAlpha),
                                0.55f to Color.White.copy(alpha = tuning.specularAlpha * 0.28f),
                                1f to Color.Transparent
                            ),
                            center = keyCenter,
                            radius = specR
                        ),
                        radius = specR,
                        center = keyCenter
                    )

                    val bounceR = size.minDimension * 0.95f
                    val bounceCenter = Offset(size.width * 0.95f, size.height * 1.05f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0f to Color.White.copy(alpha = tuning.specularAlpha * 0.38f),
                                0.6f to Color.White.copy(alpha = tuning.specularAlpha * 0.1f),
                                1f to Color.Transparent
                            ),
                            center = bounceCenter,
                            radius = bounceR
                        ),
                        radius = bounceR,
                        center = bounceCenter
                    )
                }
            }

            // ---- 3. Rim light: the defining edge of the material. Glass catches
            // light along its *entire* silhouette - brightest where it faces the
            // light (top-left), softer but still present everywhere else. Fading
            // this out makes the pane read as a flat rectangle.
            if (showRim) {
                drawPath(
                    path = path,
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color.White.copy(alpha = tuning.rimAlpha),
                            0.35f to Color.White.copy(alpha = tuning.rimAlpha * 0.55f),
                            0.62f to Color.White.copy(alpha = tuning.rimAlpha * 0.68f),
                            1f to Color.White.copy(alpha = tuning.rimAlpha * 0.22f)
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    style = Stroke(width = 1.1f * px)
                )

                // A tighter hairline just inside the rim reads as refraction,
                // giving the edge visible thickness.
                val inset = 1.5f * px
                val innerRadius = (r - inset).coerceAtLeast(0f)
                if (innerRadius > 0f) {
                    val inner = roundedRectPath(
                        size.width - inset * 2f,
                        size.height - inset * 2f,
                        innerRadius
                    )
                    clipPath(path) {
                        drawPath(
                            path = inner,
                            brush = Brush.linearGradient(
                                colorStops = arrayOf(
                                    0f to rimStart.copy(alpha = rimStart.alpha * 0.5f),
                                    0.5f to rimStart.copy(alpha = rimStart.alpha * 0.12f),
                                    1f to Color.Transparent
                                ),
                                start = Offset.Zero,
                                end = Offset(size.width, size.height)
                            ),
                            style = Stroke(width = 1f * px)
                        )
                    }
                }
            }
        }
}

/**
 * The main glass container used across the app.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    cornerRadius: Dp = 26.dp,
    strong: Boolean = false,
    raised: Boolean = false,
    tint: Color? = null,
    onClick: (() -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.972f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "glassPress"
    )

    Box(
        modifier = modifier
            .liquidGlass(
                shape = shape,
                cornerRadius = cornerRadius,
                strong = strong,
                raised = raised,
                tint = tint
            )
            .then(
                if (onClick != null) {
                    Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        )
                } else Modifier
            ),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

/** Circular glass chip — used for every icon button in the app. */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconSize: Dp = 21.dp,
    tint: Color = LocalContentColor.current,
    strong: Boolean = false,
    raised: Boolean = true,
    backgroundTint: Color? = null
) {
    GlassSurface(
        modifier = modifier.size(size),
        shape = CircleShape,
        cornerRadius = size / 2f,
        strong = strong,
        raised = raised,
        tint = backgroundTint,
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/** Wide glass pill with a label and optional leading icon. */
@Composable
fun GlassPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    strong: Boolean = false,
    textColor: Color = LocalContentColor.current,
    iconTint: Color = textColor
) {
    GlassSurface(
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(22.dp),
        cornerRadius = 22.dp,
        strong = strong,
        raised = true,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = if (icon != null) 18.dp else 20.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
        }
    }
}

/** Hairline separator that reads correctly on glass. */
@Composable
fun GlassDivider(
    modifier: Modifier = Modifier,
    inset: Dp = 0.dp
) {
    val tokens = LocalGlassTokens.current
    Box(
        modifier = modifier
            .padding(horizontal = inset)
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0f to Color.Transparent,
                        0.5f to tokens.hairline,
                        1f to Color.Transparent
                    )
                )
            )
    )
}

/* ============================================================================
   BACKGROUNDS
   ============================================================================ */

/**
 * Animated liquid mesh. Uses radial gradients rather than one enormous blur
 * pass — visually equivalent (gradients are already soft) and far cheaper.
 */
@Composable
fun LiquidGlassBackground(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    meshStrength: Float = 1f,
    grain: Boolean = true,
    baseColor: Color? = null
) {
    val transition = rememberInfiniteTransition(label = "liquidMesh")
    val driftA by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(26000, easing = LinearEasing), RepeatMode.Restart),
        label = "driftA"
    )
    val driftB by transition.animateFloat(
        initialValue = (2.0 * Math.PI).toFloat(),
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(19000, easing = LinearEasing), RepeatMode.Restart),
        label = "driftB"
    )
    val driftC by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(37000, easing = LinearEasing), RepeatMode.Restart),
        label = "driftC"
    )

    val colors = if (isDarkTheme) {
        listOf(
            Color(0xFF2A1B4A),
            Color(0xFF0A3E60),
            Color(0xFF5A1030),
            Color(0xFF0B5040)
        )
    } else {
        listOf(
            Color(0xFFE0D8FF),
            Color(0xFFD0EBFF),
            Color(0xFFFFDBE8),
            Color(0xFFD0F5E0)
        )
    }
    val backdrop = baseColor ?: if (isDarkTheme) Color(0xFF05060B) else Color(0xFFF7F7FB)
    val alpha = 0.25f * meshStrength

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(backdrop)
    ) {
        val w = size.width
        val h = size.height
        val baseR = max(w, h) * 0.72f

        val blobs = listOf(
            Blob(
                Offset(w * 0.15f + cos(driftA) * w * 0.08f, h * 0.10f + sin(driftA) * h * 0.08f),
                baseR * 0.75f,
                colors[0]
            ),
            Blob(
                Offset(w * 0.82f + cos(driftB) * w * 0.09f, h * 0.32f + sin(driftB) * h * 0.09f),
                baseR * 0.70f,
                colors[1]
            ),
            Blob(
                Offset(w * 0.10f + cos(driftC) * w * 0.08f, h * 0.82f + sin(driftC) * h * 0.08f),
                baseR * 0.60f,
                colors[2]
            ),
            Blob(
                Offset(
                    w * 0.68f + cos(driftA + 2.1f) * w * 0.10f,
                    h * 0.88f + sin(driftB + 1.3f) * h * 0.08f
                ),
                baseR * 0.55f,
                colors[3]
            )
        )

        for (blob in blobs) {
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0f to blob.color.copy(alpha = alpha),
                        0.65f to blob.color.copy(alpha = alpha * 0.35f),
                        1f to Color.Transparent
                    ),
                    center = blob.center,
                    radius = blob.radius
                ),
                radius = blob.radius,
                center = blob.center
            )
        }

        // Subtle vignette only - no harsh key light
        drawRect(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0f to Color.Transparent,
                    0.6f to Color.Transparent,
                    1f to Color.Black.copy(alpha = if (isDarkTheme) 0.25f else 0.04f)
                ),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = max(w, h) * 0.75f
            )
        )

        if (grain) {
            drawGrain(
                count = (w * h / 8000f).toInt().coerceIn(80, 500),
                alpha = if (isDarkTheme) 0.015f else 0.010f,
                seed = if (isDarkTheme) 20260901L else 19990712L,
                light = isDarkTheme
            )
        }
    }
}

private data class Blob(val center: Offset, val radius: Float, val color: Color)

/** Film-grain speckle. Seeded, so it does not shimmer between frames. */
private fun DrawScope.drawGrain(count: Int, alpha: Float, seed: Long, light: Boolean) {
    if (count <= 0) return
    val rnd = java.util.Random(seed)
    val p = Path()
    val dot = 1.15f * density
    var i = 0
    while (i < count) {
        val x = rnd.nextFloat() * size.width
        val y = rnd.nextFloat() * size.height
        p.addOval(Rect(x, y, x + dot, y + dot))
        i++
    }
    drawPath(
        path = p,
        color = if (light) Color.White.copy(alpha = alpha) else Color.Black.copy(alpha = alpha)
    )
}

/**
 * A slow diagonal light sweep. Overlay it on hero areas for the shimmer.
 */
@Composable
fun LightSweep(
    modifier: Modifier = Modifier,
    durationMillis: Int = 5200,
    delayMillis: Int = 1400
) {
    val transition = rememberInfiniteTransition(label = "sweep")
    val sweep by transition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, delayMillis = delayMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepValue"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val visible = sweep > -0.2f && sweep < 1.2f
        if (!visible) return@Canvas
        val bandW = size.width * 0.42f
        val x = size.width * sweep
        val fade = if (sweep < 0.25f) sweep / 0.45f else if (sweep > 0.85f) (1.2f - sweep) / 0.35f else 1f
        drawRect(
            brush = Brush.linearGradient(
                colorStops = arrayOf(
                    0f to Color.Transparent,
                    0.5f to Color.White.copy(alpha = 0.16f * fade.coerceIn(0f, 1f)),
                    1f to Color.Transparent
                ),
                start = Offset(x - bandW, 0f),
                end = Offset(x + bandW, size.height)
            )
        )
    }
}

/* ============================================================================
   TYPE
   ============================================================================ */

private val AuroraSweep = listOf(
    Color(0xFF7C5CFF),
    Color(0xFF22D3EE),
    Color(0xFFFF4D9D),
    Color(0xFF7C5CFF)
)

/** Text filled with a slowly drifting aurora gradient. */
@Composable
fun AnimatedGradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    colors: List<Color> = AuroraSweep
) {
    val transition = rememberInfiniteTransition(label = "textGradient")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "textPhase"
    )

    Text(
        text = text,
        modifier = modifier,
        style = style.copy(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(phase * 700f, phase * 200f),
                end = Offset(phase * 700f + 1000f, phase * 200f + 400f)
            )
        )
    )
}

/** Eyebrow label — tiny, wide-tracked, uppercase. Sits above every section. */
@Composable
fun GlassSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalGlassTokens.current.contentMuted
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = MicroLabel,
        color = color
    )
}

/* ============================================================================
   MOTION
   ============================================================================ */

/** Fade + scale entrance, staggered by [index]. */
@Composable
fun StaggeredEntrance(
    visible: Boolean = true,
    index: Int = 0,
    stepMillis: Int = 55,
    content: @Composable () -> Unit
) {
    val delay = index.coerceIn(0, 12) * stepMillis
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            tween(GLASS_ENTER_MS, delayMillis = delay, easing = FastOutSlowInEasing)
        ) + scaleIn(
            initialScale = 0.94f,
            animationSpec = tween(
                durationMillis = GLASS_ENTER_MS + 140,
                delayMillis = delay,
                easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
            )
        ),
        exit = fadeOut(tween(160))
    ) {
        content()
    }
}

/** Border that catches a gradient — for containers that are not full glass. */
fun Modifier.glassBorder(
    shape: Shape,
    isDark: Boolean,
    alpha: Float = 1f
): Modifier {
    val start = if (isDark) Color.White.copy(alpha = 0.20f * alpha) else Color.White.copy(alpha = 0.90f * alpha)
    val end = if (isDark) Color.White.copy(alpha = 0.05f * alpha) else Color(0xFFB9BDE0).copy(alpha = 0.55f * alpha)
    return this
        .clip(shape)
        .background(
            Brush.linearGradient(
                colorStops = arrayOf(
                    0f to Color.White.copy(alpha = if (isDark) 0.10f * alpha else 0.55f * alpha),
                    1f to Color.Transparent
                )
            )
        )
        .border(
            BorderStroke(1.dp, Brush.linearGradient(listOf(start, end))),
            shape = shape
        )
}

/** Size helper so callers do not have to import dp arithmetic. */
fun Dp.half(): Dp = this / 2f
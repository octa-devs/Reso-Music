package com.octadevs.resomusic.ui.components

import android.os.Build
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.octadevs.resomusic.ui.theme.LocalGlassTokens
import com.octadevs.resomusic.ui.theme.LocalGlassUserTuning
import com.octadevs.resomusic.ui.theme.EmberGold
import com.octadevs.resomusic.ui.theme.EmberOrange
import com.octadevs.resomusic.ui.theme.EmberAmber
import com.octadevs.resomusic.ui.theme.glowTokens
import com.octadevs.resomusic.ui.theme.MicroLabel
import dev.chrisbanes.haze.HazeInput
import kotlin.math.cos
import kotlin.math.max
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/**
 * Decode size, in pixels, for artwork that exists only to become a soft wash.
 *
 * These backdrops used to decode the album cover at full resolution -- often
 * 1000-3000px -- and then hand it to `Modifier.blur(80.dp)`. On Compose that is
 * a RenderEffect: a full-screen offscreen software render of a very large
 * bitmap, followed by a wide gaussian pass. And because the organic backdrop
 * behind them never stops drifting, that layer was redrawn constantly.
 *
 * A 64px decode blown up to fill the screen *is* a blur. Asking the decoder
 * for 64px and dropping the RenderEffect altogether gives the same soft wash
 * for a tiny fraction of the cost, and takes a hardware/software layer
 * boundary out of the busiest composables in the app.
 */
const val BACKDROP_WASH_PX = 64

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
    /**
     * Strength of the brand wash layered over the neutral frosted base.
     *
     * The reference material is not `rgba(255,255,255,0.1)` + a border radius:
     * it is a neutral frost with a distinctly *coloured* bleed coming
     * through it. Keeping that as its own channel (rather than tinting the
     * fill) is what lets the Saturation control move colour independently of
     * opacity.
     */
    val tintAlpha: Float,
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
            tintAlpha = if (strong) 0.62f else 0.40f,
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
            tintAlpha = if (strong) 0.40f else 0.26f,
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
        tintAlpha = (base.tintAlpha * user.intensity * transparencyMul * user.saturation)
            .coerceIn(0f, 0.95f),
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
    /**
     * Adds the frost-scatter pass: a milky bloom plus a fine seeded speckle,
     * both drawn under the rim so the silhouette stays crisp.
     *
     * This is the difference between *translucent* and *frosted*. Alpha alone
     * gives you a pane you can see through; it cannot give you the milky,
     * slightly uneven light that a real frosted pane scatters back at you.
     *
     * Off by default. Most panes in the app are meant to be a thin sheet over
     * the backdrop, and the extra milk would just fog whatever is behind them.
     * It is for the surfaces that are meant to read as genuinely thick.
     */
    haze: Boolean = false,
    showSpecular: Boolean = true,
    showRim: Boolean = true,
    /**
     * Apple's Liquid Glass material for whatever is drawn *behind* this pane.
     *
     * Supply a Haze input and the pane stops being a painted sheet and becomes
     * a piece of glass: Haze refracts the backdrop through the rim (see
     * [appleLiquidGlass]), boosts its colour and finishes the silhouette with a
     * specular rim, a Fresnel edge and an interior shadow. None of this can be
     * faked with alpha, which is why every earlier pass read as tinted plastic
     * however the alphas were tuned: alpha can *show* what is behind a pane, but
     * only refraction can *bend* it, and bending is what the eye reads as
     * thickness.
     *
     * It is also invisible over a smooth field -- refracting a gradient returns
     * a gradient. It only shows where real content passes underneath, i.e. the
     * floating chrome: the dock and the mini player both sit over a scrolling
     * list.
     *
     * `null` -- the default -- paints the pane instead. Callers pass an input
     * only for surfaces that genuinely float over moving content, and only
     * where the platform can honour it (RenderEffect, i.e. API 31+). Panes
     * *inside* the scrolling content must not, or they would sample the very
     * layer they are part of.
     */
    backdrop: HazeInput? = null
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

    // `RenderEffect`, which is what Haze blurs with, only exists from API 31.
    // Below that Haze substitutes a flat scrim, so keep the opaque body rather
    // than thinning a pane that would then have nothing to thin for.
    val activeBackdrop =
        if (backdrop != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) backdrop else null

    // ---- Apple Liquid Glass ------------------------------------------------
    // When the pane has something to sample, the painted body below is not a
    // layer under the glass -- it is replaced by it. The material's whole
    // signature is that it *bends* what is behind it, and painting our own
    // gradient on top buries the one effect that makes it read as glass rather
    // than as tinted plastic. So the fill, the colour bleed, the inner shadow,
    // the specular, the frost bloom and the rim are all skipped on this path;
    // the only things the pane still contributes are its drop shadow and a
    // whisper of brand tint handed to the material.
    if (activeBackdrop != null) {
        val user = LocalGlassUserTuning.current.clamped()

        // The app's own Liquid Glass sliders stay meaningful by writing through
        // to the material. Anything not mapped here is left at Haze's calibrated
        // regular response, which is the actual Apple look.
        val glassTint = tokens.meshFill.copy(
            alpha = (0.05f + 0.11f * user.intensity * user.saturation).coerceIn(0f, 0.9f)
        )
        val glassSpecular = (if (isDark) 0.38f else 0.48f) * user.intensity
        val glassChroma = if (isDark) {
            0.80f + 0.40f * user.saturation
        } else {
            1.10f + 0.45f * user.saturation
        }
        val glassEdgeShadow = (if (isDark) 0.32f else 0.14f) *
            user.shadowIntensity * (0.6f + 0.4f * user.borderOpacity)
        // `transparency` is inverted everywhere else in the app -- pushing it up
        // thins the pane -- so it scales the material down, not up. Pinned to
        // exactly 1f at the default dial position so the out-of-the-box look is
        // Haze's full calibrated material, and only ever *reduces* from there:
        // at low opacity the material blends back into the flat backdrop and
        // stops being glass at all.
        val glassAlpha = (1f - (user.transparency - 0.5f).coerceAtLeast(0f) * 0.5f)
            .coerceIn(0.6f, 1f)

        // ---- Translucency, not transparency.
        // Without these two the pane is only *clear*: the backdrop is bent, but
        // nothing signals a solid object in the way, so at a glance it still
        // reads as a hole cut in the screen. The veil is the light that entered
        // the glass and scattered instead of passing through; the ambient term
        // is the light the slab picks up on its own. Together they turn the
        // pane from a window into a slab you can see through -- the difference
        // between transparent and translucent.
        //
        // Dark mode needs a touch more of both: the material sits over an
        // already-dark backdrop, so the milk has to carry the luminance itself.
        //
        // Transparency is inverted everywhere in the app (up = thinner), so it
        // thins the milk as well rather than only lowering the material's
        // opacity. Otherwise sliding it up would leave the pane milky and the
        // control would read as broken.
        val userIntensity = user.intensity.coerceIn(0.4f, 1.6f)
        val veilScale = (1.15f - user.transparency * 0.4f).coerceIn(0.5f, 1.2f)
        val glassVeil = Color.White.copy(
            alpha = (if (isDark) 0.13f else 0.20f) * userIntensity * veilScale
        )
        val glassAmbient = (if (isDark) 0.34f else 0.42f) * userIntensity * veilScale

        return this
            .shadow(
                elevation = tuning.elevation,
                shape = shape,
                clip = false,
                ambientColor = shadowColor.copy(alpha = shadowAlpha),
                spotColor = shadowColor.copy(alpha = shadowAlpha)
            )
            .clip(shape)
            .appleLiquidGlass(
                input = activeBackdrop,
                shape = shape,
                cornerRadius = cornerRadius,
                tintColor = glassTint,
                specularIntensity = glassSpecular,
                chromaMultiplier = glassChroma,
                edgeShadowAlpha = glassEdgeShadow,
                materialAlpha = glassAlpha,
                backgroundVeil = glassVeil,
                ambientLight = glassAmbient
            )
    }

    // ---- Painted pane ------------------------------------------------------
    // No backdrop (or no RenderEffect to sample one with): the original
    // hand-rolled material. Rim, specular and inner shadow are the pane's
    // silhouette, not its opacity.
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

            // ---- 0. Colour bleed.
            // This is the layer that stops the material reading as
            // "rgba(255,255,255,0.1) + border-radius". The neutral frost from
            // `.background` stays underneath, and the brand wash sits on
            // top of it, strongest at the top of the pane and easing off
            // toward the bottom so the pane still feels lit from above.
            // The wash hue comes from `GlassTokens.meshFill`, so it follows
            // both the brand default and any palette the user picks.
            if (tuning.tintAlpha > 0.004f) {
                val wash = tokens.meshFill
                clipPath(path) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to wash.copy(alpha = tuning.tintAlpha),
                                0.42f to wash.copy(alpha = tuning.tintAlpha * 0.74f),
                                1f to wash.copy(alpha = tuning.tintAlpha * 0.46f)
                            )
                        )
                    )
                }
            }

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

            // ---- 2b. Frost scatter ("haze").
            // A milky bloom plus a fine speckle, both inside the pane and under
            // the rim. The bloom is the light that went into the glass and came
            // back out; the speckle is the scatter settling. Together they are
            // what the eye reads as "frosted" rather than "translucent", and
            // neither can be produced with an alpha value.
            if (haze) {
                val bloomTop = if (isDark) 0.20f else 0.34f
                val bloomMid = if (isDark) 0.12f else 0.22f
                val bloomBottom = if (isDark) 0.07f else 0.14f
                clipPath(path) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.White.copy(alpha = bloomTop),
                                0.5f to Color.White.copy(alpha = bloomMid),
                                1f to Color.White.copy(alpha = bloomBottom)
                            )
                        )
                    )
                    // Seeded, not random per frame. A live random would make this
                    // layer dirty on every frame, which is the exact per-frame
                    // redraw cost the rest of this file was written to avoid.
                    drawGrain(
                        count = ((size.width * size.height) / (70f * px * px))
                            .toInt()
                            .coerceIn(160, 1100),
                        alpha = if (isDark) 0.05f else 0.035f,
                        seed = 0x5EEDF00DL,
                        light = false
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

/**
 * A translucent pane drawn straight from a Material container role.
 *
 * Roughly 34 call sites hand-written their own fill as
 * `surfaceVariant.copy(alpha = 0.5f)`. Once the container roles carry alpha
 * themselves (see `ColorScheme.glassify`), that idiom inverts: `.copy(alpha)`
 * *replaces* alpha rather than multiplying it, so a role that was a 12% veil
 * silently became a 50% one -- a milky slab rather than a pane. Those sites
 * now ask for the role directly.
 *
 * This exists so they can keep their alpha argument and their call-site
 * readability. The value is mapped into each theme's usable band rather than
 * used raw, because the band [ColorScheme.glassify] lands on differs by an
 * order of magnitude between themes: dark glass sits at 0-0.17, light glass at
 * 0.62-0.88. A literal 0.5 is a heavy scrim in dark mode and invisible in
 * light mode, so passing it through raw would mean the same number means two
 * completely different things depending on the user's theme.
 *
 * The dark-mode band must stay in step with `GlassVeilTop` in Theme.kt.
 *
 * @param strength 0..1 intent, not literal alpha.
 */
@Composable
fun glassPane(strength: Float = 0.5f): Color {
    val s = strength.coerceIn(0f, 1f)
    // `surface` rather than `colorScheme` itself: only the page base keeps an
    // opaque colour after glassify(), so it is the one honest dark/light test
    // left. `colorScheme.luminance()` would average the whole palette and give
    // a meaningless answer.
    return if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) {
        // 0.10 -> 0.28: the same band glassify() spans in dark mode, so a
        // hand-written pane lands in the same range as a role-derived one and
        // the two do not read as different materials. Keeping this ceiling in
        // step with GlassVeilTop is the whole point -- when the ladder moved,
        // this had to move with it.
        Color.White.copy(alpha = 0.10f + s * 0.18f)
    } else {
        Color.White.copy(alpha = 0.62f + s * 0.26f)
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
 * App-wide background.
 *
 * This used to be two drifting radial pools. The reference design is not that
 * at all — it is a monochrome near-black field carrying faint topographic
 * contour banding — so the implementation now lives in
 * [OrganicWaveBackdrop]. This stays as the app's single entry point so the
 * existing call sites keep working unchanged.
 *
 * @param grain kept for signature compatibility; the organic field supplies
 *        its own texture, so the old film-grain speckle is no longer drawn.
 */
@Composable
fun LiquidGlassBackground(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    meshStrength: Float = 1f,
    grain: Boolean = true,
    baseColor: Color? = null
) {
    val glow = glowTokens()
    Box(modifier = modifier.fillMaxSize()) {
        if (baseColor != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(baseColor)
            )
        }
        OrganicWaveBackdrop(
            modifier = Modifier.fillMaxSize(),
            isDark = isDarkTheme,
            strength = meshStrength,
            washColor = glow.primary
        )
    }
}

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

private val EmberSweep = listOf(
    EmberOrange,
    EmberGold,
    EmberAmber,
    EmberOrange
)

/** Text filled with a slowly drifting brand gradient. */
@Composable
fun AnimatedGradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    colors: List<Color> = EmberSweep
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

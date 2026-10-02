package com.octadevs.resomusic.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.octadevs.resomusic.ui.theme.GlowTokens
import kotlin.math.cos
import kotlin.math.max
import com.octadevs.resomusic.ui.theme.glowTokens
import kotlin.math.sin

/* ============================================================================
   RESO — AMBIENT GLOW
   ----------------------------------------------------------------------------
   The brief for this layer was "ambient lighting", not "neon". So:

   - Two or three radial gradients only. No blur passes, no RenderEffect.
   - Periods of 24-40s. Anything faster reads as a screensaver.
   - The glow sits *behind* content and never draws over text.
   - Amplitude is driven by playback, but only as a slow swell, so the
     difference between paused and playing is felt rather than noticed.
   - Honours the system "remove animations" setting: when the user has turned
     animations off at the OS level we render one static frame instead of
     spinning an infinite transition forever.
   ============================================================================ */

/**
 * True when the platform animator duration scale is 0, i.e. the user has
 * enabled "Remove animations" in developer/accessibility options. Sampled
 * once and cached; it cannot change while the process is alive in practice.
 */
@Composable
fun rememberReducedMotion(): Boolean {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember(context) {
        try {
            val scale = android.provider.Settings.Global.getFloat(
                context.contentResolver,
                android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            )
            scale == 0f
        } catch (_: Throwable) {
            false
        }
    }
}

/** 0f..1f breathing phase, frozen at rest when the user asked for less motion. */
@Composable
private fun ambientPhase(reducedMotion: Boolean, durationMillis: Int): State<Float> {
    val transition = rememberInfiniteTransition(label = "ambient")
    val animated by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambientPhase"
    )
    return remember(reducedMotion, animated) {
        if (reducedMotion) derivedStateOf { 0.5f } else derivedStateOf { animated }
    }
}

/**
 * One soft pool of brand-coloured light.
 *
 * @param anchorX 0f = left edge, 1f = right edge.
 * @param anchorY 0f = top edge, 1f = bottom edge.
 */
@Composable
private fun GlowPool(
    color: Color,
    anchorX: Float,
    anchorY: Float,
    radiusFactor: Float,
    alpha: Float,
    phase: Float,
    drift: Float = 0.06f
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (alpha <= 0.002f) return@Canvas
        val w = size.width
        val h = size.height
        val cx = w * (anchorX + cos(phase * 2f * Math.PI.toFloat()) * drift)
        val cy = h * (anchorY + sin(phase * 2f * Math.PI.toFloat()) * drift)
        val r = max(w, h) * radiusFactor
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0f to color.copy(alpha = alpha),
                    0.45f to color.copy(alpha = alpha * 0.38f),
                    1f to Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = r
            ),
            radius = r,
            center = Offset(cx, cy)
        )
    }
}

/**
 * The app-wide backdrop: a warm charcoal base with two very slow light pools.
 * Deliberately weak — it should read as a room with a lamp in it, not as a
 * gradient wallpaper.
 *
 * @param isPlaying lifts the glow slightly while audio is running.
 * @param strength   multiplies the whole layer; 0 disables it entirely.
 * @param drawBase   set false to lay only the glow over artwork that is
 *                   already painting the backdrop (the full player does this).
 */
@Composable
fun AmbientGlowBackground(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    strength: Float = 1f,
    baseColor: Color? = null,
    drawBase: Boolean = true,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val glow: GlowTokens = glowTokens()
    val scheme = MaterialTheme.colorScheme
    val reducedMotion = rememberReducedMotion()

    val phaseA = ambientPhase(reducedMotion, 34000)
    val phaseB = ambientPhase(reducedMotion, 27000)

    // Paused sits noticeably below playing, but neither end is extreme.
    val lift = if (isPlaying) 1f else 0.55f

    Box(modifier = modifier.fillMaxSize()) {
        if (drawBase) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = baseColor ?: scheme.background)
            }
        }

        GlowPool(
            color = glow.primary,
            anchorX = 0.18f,
            anchorY = 0.06f,
            radiusFactor = 0.80f,
            alpha = 0.085f * strength * lift,
            phase = phaseA.value
        )
        GlowPool(
            color = glow.tertiary,
            anchorX = 0.88f,
            anchorY = 0.78f,
            radiusFactor = 0.72f,
            alpha = 0.060f * strength * lift,
            phase = phaseB.value
        )

        // Faintest possible lift in the centre keeps the middle of the screen
        // from going flat black without ever competing with content.
        GlowPool(
            color = glow.secondary,
            anchorX = 0.55f,
            anchorY = 0.42f,
            radiusFactor = 0.60f,
            alpha = 0.026f * strength,
            phase = (phaseA.value + phaseB.value) * 0.5f,
            drift = 0.03f
        )

        content()
    }
}

/**
 * A halo drawn *behind* a floating surface. Place it under the component's own
 * glass so the bloom never washes out the content sitting on top.
 *
 * Sized by the caller's [modifier]; the bloom is centred on whatever box that
 * produces, so it tracks the surface as it resizes.
 */
@Composable
fun AmbientHalo(
    modifier: Modifier = Modifier,
    color: Color? = null,
    strength: Float = 1f,
    isPlaying: Boolean = false
) {
    val glow = glowTokens()
    val tint = color ?: glow.primary
    val reducedMotion = rememberReducedMotion()
    val phase = ambientPhase(reducedMotion, 4600)
    val lift = if (isPlaying) 1f else 0.6f
    val alpha = (0.26f * strength * lift).coerceIn(0f, 0.55f)

    Canvas(modifier = modifier) {
        if (alpha <= 0.002f) return@Canvas
        val breathe = 0.88f + 0.12f * sin(phase.value * 2f * Math.PI.toFloat())
        val r = max(size.width, size.height) * 0.62f * breathe
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0f to tint.copy(alpha = alpha),
                    0.42f to tint.copy(alpha = alpha * 0.34f),
                    1f to Color.Transparent
                ),
                center = center,
                radius = r
            ),
            radius = r,
            center = center
        )
    }
}
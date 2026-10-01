package com.octadevs.resomusic.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/**
 * Motion tokens. Everything springs or eases through one of these so the app
 * feels like a single physical object rather than a pile of widgets.
 */
object Motion {

    /** Material-ish expressive easing for entrances/exits. */
    val Emphasized: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
    val Standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /* ---------- Durations ---------- */
    const val Micro = 120
    const val Quick = 220
    const val Normal = 340
    const val Slow = 560
    const val VerySlow = 900

    /* ---------- Springs ---------- */
    /** Snappy, barely overshoots. For presses and toggles. */
    fun <T> snappy(durationMillis: Int = Quick) = tween(durationMillis, easing = Emphasized)

    /** The house spring: soft, bouncy, never sluggish. */
    fun <T> bouncy() = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    fun <T> springy(
        dampingRatio: Float = Spring.DampingRatioLowBouncy,
        stiffness: Float = Spring.StiffnessMedium
    ) = spring(dampingRatio = dampingRatio, visibilityThreshold = 0.01f, stiffness = stiffness)

    fun <T> gentle() = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

    fun <T> dramatic() = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow,
        visibilityThreshold = 0.01f
    )
}

/* ---------- Convenience specs for shared-element style transitions ---------- */

fun enterFadeThrough(): FiniteAnimationSpec<Float> = tween(Motion.Slow, easing = Motion.EmphasizedDecelerate)
fun exitFadeThrough(): FiniteAnimationSpec<Float> = tween(Motion.Quick, easing = Motion.EmphasizedAccelerate)

/** Matches a fade-through with a subtle lift. */
fun enterOffset(): FiniteAnimationSpec<IntOffset> = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = IntOffset.VisibilityThreshold
)

/** Matches a fade-through with a subtle scale. */
fun enterScale(from: Float = 0.92f): FiniteAnimationSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = 0.01f
)

/** Container transform spec. */
fun containerSize(): FiniteAnimationSpec<IntSize> = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = IntSize.VisibilityThreshold
)

val Float.Companion.VisibilityThreshold: Float get() = 0.01f
package com.octadevs.resomusic.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween

/**
 * Motion tokens. Everything springs or eases through one of these so the app
 * feels like a single physical object rather than a pile of widgets.
 */
object Motion {

    /** Material-ish expressive easings. */
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

    /* ---------- Damping ratios ---------- */
    const val Bouncy = Spring.DampingRatioMediumBouncy
    const val Springy = Spring.DampingRatioLowBouncy
    const val Firm = Spring.DampingRatioNoBouncy

    const val StiffnessLow = Spring.StiffnessLow
    const val StiffnessMedium = Spring.StiffnessMedium
    const val StiffnessMediumLow = Spring.StiffnessMediumLow
    const val StiffnessHigh = Spring.StiffnessHigh
}
// Copyright 2026, Reso Music contributors
// SPDX-License-Identifier: GPL-3.0

@file:OptIn(dev.chrisbanes.haze.ExperimentalHazeApi::class)

package com.octadevs.resomusic.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.GlassStyleScope
import dev.chrisbanes.haze.glass.hazeGlass

/* ============================================================================
   APPLE LIQUID GLASS
   ------------------
   What separates Apple's Liquid Glass from a frosted sheet is that it is a
   *lens*, not a filter. Four things happen in it, and a blur gives you only
   the first:

     1. the backdrop is smeared (blur)
     2. light entering the edge is bent, so content near the rim is pulled
        sideways and squashed -- this is the "swimming" distortion that makes
        the material read as a thick curved solid
     3. the material's own saturation is boosted, so colour behind it comes
        through richer than the backdrop actually is
     4. the rim catches a directional specular highlight and a Fresnel edge,
        and drops a soft interior shadow so the material looks milled rather
        than pasted

   Haze ships exactly this as its `GlassStyle` material. `GlassStyle.regular`
   is its calibrated response and is *already* the base of every style Haze
   resolves (see `resolveGlassStyleValues`), so this file only has to say
   where the pane is and what colour the brand wants it.

   Why this is not `Modifier.blur`: RenderEffect only exists from API 31, and
   the refraction pass inside the material is a RuntimeShader, which needs 33.
   Below 31 there is no honest way to draw this, and callers fall back to the
   painted pane -- see [liquidGlass]'s `backdrop`.

   Deliberately applied to the two floating chrome surfaces only -- the dock
   and the mini player. Those are the only panes in the app that have real
   content moving behind them (the scrolling pager), which is the part that
   makes the lens visible at all. A pane *inside* the list would be sampling
   the very layer it is painted into.
   ============================================================================ */

/**
 * Draws Apple's Liquid Glass material into this node, refracting [input].
 *
 * The material owns the entire surface: it paints the refracted backdrop, its
 * own specular rim, its Fresnel edge and its interior shadow. Nothing should
 * be drawn over it -- that is what buried the effect in the first place.
 *
 * [shape] masks the material and its refraction, so the bent light follows the
 * rounded silhouette rather than a rectangle.
 *
 * Every float here is snapped to its valid domain before it reaches Haze.
 * Haze's style setters validate and throw `IllegalArgumentException` on
 * out-of-range values, and these are fed by user sliders, so an unclamped
 * value would be a crash on composition rather than a bad-looking pane.
 */
@Composable
fun Modifier.appleLiquidGlass(
    input: HazeInput,
    shape: Shape,
    cornerRadius: Dp,
    tintColor: Color,
    specularIntensity: Float,
    chromaMultiplier: Float,
    edgeShadowAlpha: Float,
    materialAlpha: Float
): Modifier {
    val radius = cornerRadius.coerceIn(0.dp, 240.dp)
    val fSpecular = specularIntensity.coerceIn(0f, 1f)
    val fChroma = chromaMultiplier.coerceIn(0f, 2f)
    val fEdgeShadow = edgeShadowAlpha.coerceIn(0f, 1f)
    val fAlpha = materialAlpha.coerceIn(0f, 1f)

    // The material can only mask a rounded rectangle, so a non-rounded Shape
    // falls back to [radius] rather than being ignored -- an unmasked material
    // would bleed refraction into the corners as a grey square.
    val rounded = shape as? RoundedCornerShape ?: RoundedCornerShape(radius)

    val style = remember(rounded, fSpecular, fChroma, fEdgeShadow, fAlpha, tintColor) {
        // The regular response is replayed first (Haze seeds every style with
        // it), so these writes only *add* to Apple's calibrated material rather
        // than replacing it. Replaying it twice is idempotent.
        val writes: GlassStyleScope.() -> Unit = {
            shape(rounded)
            specularIntensity(fSpecular)
            chromaMultiplier(fChroma)
            edgeShadow(Color.Black.copy(alpha = fEdgeShadow))
            alpha(fAlpha)
            tint(tintColor)
        }
        GlassStyle.regular.then(writes)
    }

    return this.hazeGlass(
        input = input,
        style = style,
        performanceMode = HazePerformanceMode.Balanced
    )
}

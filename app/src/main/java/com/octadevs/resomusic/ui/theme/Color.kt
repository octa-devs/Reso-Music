package com.octadevs.resomusic.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/* ============================================================
   RESO MUSIC — DESIGN TOKENS
   A single source of truth for colour. Everything else in the UI
   derives from here so the whole app stays visually coherent.
   ============================================================ */

/* ---------- Legacy Material basics (kept for compatibility) ---------- */
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

/* ============================================================
   RESOMUSIC BRAND — "Noir + Ember"
   ------------------------------------------------------------
   The accent ramp is sampled from the ResoMusic logo asset
   itself (new_reso_logo.jpg, 1024x1024), not from any reference
   mockup. Measuring that file:

     · 86.6% of its pixels are pure #000000 (the square canvas)
     · the mark itself ramps  hue 17deg -> 54deg
     · core of the mark       #F07000  (hue 28, sat 97%)
     · ramp top               #F0E050  (hue 54, warm gold)

   So the brand is a warm ember ramp, and the structural surface
   around it stays monochrome near-black. That is the whole idea:
   black structure, ember light coming through the glass.

   The tokens below are that ramp, rounded to the nearest clean
   step. EmberOrange is the signature; EmberAmber and EmberGold
   are the highlights that keep large ember areas from going flat.
   ============================================================ */

/** The signature glass tint. Used for glass fills, active pills, progress. */
val EmberOrange = Color(0xFFFF6B1A)

/** Lifted ember — active/pressed states and glowing icon accents. */
val EmberAmber = Color(0xFFFFA21A)

/** Secondary chromatic voice: the gold top of the logo's ramp. */
val EmberGold = Color(0xFFFFC542)

/** Deep ember tertiary — the #E04000 end of the logo's ramp. */
val EmberOrangeDeep = Color(0xFFE64A0C)

/** Deep warm shadow — glass shadow and contour shading, never a hue. */
val EmberShadow = Color(0xFF3A1A08)

/** Pale gold for text that sits directly on a glass pane. */
val EmberGoldSoft = Color(0xFFFFDE7A)

/** Multi-stop ramp for the rare places a gradient is warranted. */
val EmberRamp = listOf(EmberOrange, EmberAmber, EmberGold)

/* ---------- Brand core: "Aurora" (legacy, still referenced by palettes) ---------- */
val AuroraViolet = Color(0xFF7C5CFF)
val AuroraIndigo = Color(0xFF4F46E5)
val AuroraCyan = Color(0xFF22D3EE)
val AuroraPink = Color(0xFFFF4D9D)
val AuroraAmber = Color(0xFFFFB020)
val AuroraMint = Color(0xFF34D399)

/* ---------- Dark spectrum ----------
   True neutral black, not blue-black and not warm charcoal. The
   reference's background sampled as exactly #000000 at ~47% of
   pixels with only #1A1A1A→#2E2E2E variation on top, so the
   neutrals here are desaturated to match. All the perceived colour
   in the app comes from the purple glass layered over this. */
val NoirAbyss = Color(0xFF000000)
val NoirDeep = Color(0xFF050506)
val NoirBase = Color(0xFF0B0B0D)
val NoirElevated = Color(0xFF121214)
val NoirHigh = Color(0xFF191920)
val NoirOutline = Color(0xFF24242B)

/* ---------- Light spectrum ---------- */
val DayMist = Color(0xFFF7F7FB)
val DayBase = Color(0xFFFCFCFF)
val DayElevated = Color(0xFFFFFFFF)
val DayHigh = Color(0xFFF1F2F9)
val DayOutline = Color(0xFFD9DBE8)

/* ---------- Glass physics ---------- */
/** Edge light that makes glass look like it has thickness. */
val GlassRimDark = Color(0x66FFFFFF)
val GlassRimLight = Color(0x40FFFFFF)

/** Specular highlight (the "hot spot" that sells liquid glass). */
val SpecularDark = Color(0x8CFFFFFF)
val SpecularLight = Color(0xB3FFFFFF)

/** Inner shadow used to carve depth into a pane. */
val GlassInnerShadowDark = Color(0x40000000)
val GlassInnerShadowLight = Color(0x1A000000)

/* ---------- Organic backdrop ----------
   The reference background is a monochrome field: pure #000000 with
   faint contour banding around #1A1A1A–#2E2E2E. These stops recreate
   that measured range rather than guessing a tint. */
val ContourNear = Color(0xFF2E2E2E)
val ContourMid = Color(0xFF1A1A1A)
val ContourFar = Color(0xFF101012)

/** Rare chromatic wash drifting across the backdrop. Logo ember, held faint. */
val AmbientWash = Color(0x1AFF6B1A)

/* ---------- Semantic accents ---------- */
val AccentWarm = Color(0xFFFF7A59)
val AccentCool = Color(0xFF35C4FF)
val AccentGrape = Color(0xFFB15CFF)
val AccentLime = Color(0xFFB4F14C)

/* ---------- Useful alpha ramps ---------- */
fun Color.a(alpha: Float) = copy(alpha = alpha)

/**
 * Spacing scale. Every gap in the app should come from here so rhythm stays
 * consistent instead of drifting across ~20 screens.
 */
object Space {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val huge = 48.dp
}

/**
 * Corner-radius scale. Glass panes in particular need their radius to match
 * the radius passed to `liquidGlass`, or the rim highlight traces the wrong
 * silhouette.
 */
object Radius {
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 26.dp
    val xxl = 32.dp
    val pill = 999.dp
}
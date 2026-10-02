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

/* ---------- Brand core: "Aurora" ---------- */
val AuroraViolet = Color(0xFF7C5CFF)
val AuroraIndigo = Color(0xFF4F46E5)
val AuroraCyan = Color(0xFF22D3EE)
val AuroraPink = Color(0xFFFF4D9D)
val AuroraAmber = Color(0xFFFFB020)
val AuroraMint = Color(0xFF34D399)

/* ---------- Dark spectrum ---------- */
val NightAbyss = Color(0xFF05060B)
val NightDeep = Color(0xFF0A0C14)
val NightBase = Color(0xFF10121C)
val NightElevated = Color(0xFF171A26)
val NightHigh = Color(0xFF1F2331)
val NightOutline = Color(0xFF2C3142)

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
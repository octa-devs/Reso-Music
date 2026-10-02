package com.octadevs.resomusic.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.octadevs.resomusic.R

/**
 * Reso uses a rounded geometric family (Quicksand) for *everything*.
 *
 * Only two real faces ship in `res/font` (regular + bold), so before this
 * change every `FontWeight.Medium` / `SemiBold` / `ExtraBold` in the
 * codebase was being faux-bolded by the platform — which is exactly why the
 * type looked thin and uneven next to genuinely-bold text. Mapping the
 * intermediate weights onto the real bold face removes all synthesis and
 * makes the rounded character of the family actually read.
 */
val quicksand = FontFamily(
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_regular, FontWeight.Medium),
    Font(R.font.quicksand_bold, FontWeight.SemiBold),
    Font(R.font.quicksand_bold, FontWeight.Bold),
    Font(R.font.quicksand_bold, FontWeight.ExtraBold)
)

private val TightLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None
)

private fun display(size: Int, tracking: Double, weight: FontWeight = FontWeight.Bold) = TextStyle(
    fontFamily = quicksand,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (size * 1.02).sp,
    letterSpacing = tracking.sp,
    lineHeightStyle = TightLineHeight
)

private fun headline(size: Int, tracking: Double, weight: FontWeight = FontWeight.Bold) = TextStyle(
    fontFamily = quicksand,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (size * 1.12).sp,
    letterSpacing = tracking.sp,
    lineHeightStyle = TightLineHeight
)

private fun body(size: Int, tracking: Double, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = quicksand,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (size * 1.45).sp,
    letterSpacing = tracking.sp
)

private fun label(size: Int, tracking: Double, weight: FontWeight = FontWeight.Bold) = TextStyle(
    fontFamily = quicksand,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (size * 1.25).sp,
    letterSpacing = tracking.sp
)

/**
 * Set of Material typography styles.
 *
 * The previous scale peaked at 64sp display with -2sp tracking — that reads
 * as "SaaS marketing site", not "music app". The reference design gets its
 * personality from *weight and roundness* on moderate sizes, not from sheer
 * size, so the display end has been pulled back and the headings made
 * uniformly bold. Everything stays opt-in per style so existing screens keep
 * working; new UI should prefer the expressive helpers below.
 */
val Typography = Typography(
    displayLarge = display(42, -1.0),
    displayMedium = display(34, -0.8),
    displaySmall = display(28, -0.6),

    headlineLarge = headline(27, -0.5),
    headlineMedium = headline(23, -0.4),
    headlineSmall = headline(20, -0.2),

    titleLarge = headline(19, -0.2),
    titleMedium = label(16, 0.0, FontWeight.Bold),
    titleSmall = label(14, 0.1),

    bodyLarge = body(16, 0.1),
    bodyMedium = body(14, 0.15),
    bodySmall = body(12, 0.2),

    labelLarge = label(14, 0.2),
    labelMedium = label(12, 0.4),
    labelSmall = label(11, 0.6)
)

/* ============================================================
   EXPRESSIVE TYPE EXTENSIONS
   Not part of Material's scale — used for the "hero" moments.
   ============================================================ */

private fun heroStyle(size: Int, tracking: Double) = TextStyle(
    fontFamily = quicksand,
    fontWeight = FontWeight.Bold,
    fontSize = size.sp,
    lineHeight = (size * 0.98).sp,
    letterSpacing = tracking.sp,
    lineHeightStyle = TightLineHeight
)

/** Big poster numerals / letters. Negative tracking, very tight leading. */
val HeroDisplay = heroStyle(38, -1.4)

/**
 * Section heading — "Recently Played", "Your Playlists".
 *
 * This is the workhorse of the reference design: bold, rounded, and clearly
 * present without tipping into oversized display type. Kept separate from
 * `headlineMedium` so section rhythm can be tuned in one place.
 */
val DisplayTitle = heroStyle(22, -0.5)

/** Card titles over artwork. Must stay legible on a busy image. */
val CardTitle = TextStyle(
    fontFamily = quicksand,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = (-0.2).sp
)

/** Secondary metadata line under a card title — artist, count, duration. */
val CardSubtitle = TextStyle(
    fontFamily = quicksand,
    fontWeight = FontWeight.Medium,
    fontSize = 12.5.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.1.sp
)

/** Category filter pills. */
val PillLabel = TextStyle(
    fontFamily = quicksand,
    fontWeight = FontWeight.Bold,
    fontSize = 13.sp,
    lineHeight = 17.sp,
    letterSpacing = 0.1.sp
)

/** Section eyebrow: tiny, bold, wide tracking — reads like a hardware label. */
val MicroLabel = TextStyle(
    fontFamily = quicksand,
    fontWeight = FontWeight.Bold,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 2.4.sp
)

/** Slightly larger eyebrow for sheets and cards. */
val Eyebrow = TextStyle(
    fontFamily = quicksand,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 1.8.sp
)

/** Tabular-feeling style for durations, bitrates, counters. */
val Numeric = TextStyle(
    fontFamily = quicksand,
    fontWeight = FontWeight.Bold,
    fontSize = 13.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp
)

/** Gradient-filled brand text. */
fun gradientTextStyle(colors: List<androidx.compose.ui.graphics.Color>): TextStyle =
    HeroDisplay.copy(brush = Brush.linearGradient(colors))

/** All-caps transform helper for eyebrows. */
fun String.eyebrowCase(): String = uppercase()
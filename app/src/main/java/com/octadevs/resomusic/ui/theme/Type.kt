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
 * Reso uses a rounded geometric family (Quicksand) for *everything*, but the
 * scale is deliberately extreme: display sizes are huge with tight negative
 * tracking for that "editorial poster" feel, while micro labels are tiny,
 * uppercase and wide-tracked for that "instrument panel" feel.
 */

val quicksand = FontFamily(
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_bold, FontWeight.Bold)
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
 * Set of Material typography styles. Everything is opt-in per style so existing
 * screens keep working, but new UI should prefer the expressive helpers below.
 */
val Typography = Typography(
    displayLarge = display(64, -2.0),
    displayMedium = display(50, -1.6),
    displaySmall = display(40, -1.2),

    headlineLarge = headline(34, -0.9),
    headlineMedium = headline(28, -0.6),
    headlineSmall = headline(23, -0.4),

    titleLarge = headline(21, -0.3),
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
val HeroDisplay = heroStyle(46, -2.2)

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
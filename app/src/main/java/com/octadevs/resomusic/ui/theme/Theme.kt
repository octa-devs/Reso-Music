package com.octadevs.resomusic.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/* ============================================================
   1. SIGNATURE SCHEMES — "Aurora"
   Deep space blacks with neon-violet/cyan energy.
   ============================================================ */

private val AuroraDark = darkColorScheme(
    primary = AuroraViolet,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF3B2C6B),
    onPrimaryContainer = Color(0xFFE7DEFF),

    secondary = AuroraCyan,
    onSecondary = Color(0xFF00323B),
    secondaryContainer = Color(0xFF0B4A56),
    onSecondaryContainer = Color(0xFFB9F1FF),

    tertiary = AuroraPink,
    onTertiary = Color(0xFF4A0026),
    tertiaryContainer = Color(0xFF6E1245),
    onTertiaryContainer = Color(0xFFFFD9E7),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = NightDeep,
    onBackground = Color(0xFFECECF2),
    surface = NightDeep,
    onSurface = Color(0xFFECECF2),
    surfaceVariant = NightElevated,
    onSurfaceVariant = Color(0xFFA9ACC0),
    surfaceTint = AuroraViolet,

    inverseSurface = Color(0xFFECECF2),
    inverseOnSurface = Color(0xFF1B1B22),
    inversePrimary = Color(0xFF5B45C9),

    surfaceDim = NightAbyss,
    surfaceBright = NightHigh,
    surfaceContainerLowest = NightAbyss,
    surfaceContainerLow = NightBase,
    surfaceContainer = NightBase,
    surfaceContainerHigh = NightElevated,
    surfaceContainerHighest = NightHigh,

    outline = Color(0xFF474B60),
    outlineVariant = NightOutline,
    scrim = Color(0xFF000000)
)

private val AuroraLight = lightColorScheme(
    primary = Color(0xFF5B45C9),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE7DEFF),
    onPrimaryContainer = Color(0xFF1F0073),

    secondary = Color(0xFF006877),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB9F1FF),
    onSecondaryContainer = Color(0xFF001F26),

    tertiary = Color(0xFFB3235C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD9E7),
    onTertiaryContainer = Color(0xFF3E0021),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = DayBase,
    onBackground = Color(0xFF16141F),
    surface = DayBase,
    onSurface = Color(0xFF16141F),
    surfaceVariant = DayHigh,
    onSurfaceVariant = Color(0xFF5A5C6E),
    surfaceTint = Color(0xFF5B45C9),

    inverseSurface = Color(0xFF2B2936),
    inverseOnSurface = Color(0xFFF2F0FA),
    inversePrimary = Color(0xFFCBBDFF),

    surfaceDim = Color(0xFFDDDCE8),
    surfaceBright = DayBase,
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = DayMist,
    surfaceContainer = DayMist,
    surfaceContainerHigh = DayElevated,
    surfaceContainerHighest = DayHigh,

    outline = Color(0xFF7A7C8E),
    outlineVariant = DayOutline,
    scrim = Color(0xFF000000)
)

/* ============================================================
   2. SOFT PREDEFINED PALETTES (user-selectable)
   ============================================================ */

// 1. Sunset Peach
private val SunsetPeachLight = lightColorScheme(
    primary = Color(0xFF9C3F2C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD1),
    onPrimaryContainer = Color(0xFF3A0A00),
    secondary = Color(0xFF775651),
    secondaryContainer = Color(0xFFFFDAD1),
    tertiary = Color(0xFF6C5D10),
    tertiaryContainer = Color(0xFFF6E08A)
)
private val SunsetPeachDark = darkColorScheme(
    primary = Color(0xFFFFB4A0),
    onPrimary = Color(0xFF5C1907),
    primaryContainer = Color(0xFF7D2B17),
    secondary = Color(0xFFE7BDB5),
    secondaryContainer = Color(0xFF5D3F3A),
    tertiary = Color(0xFFDAC56F),
    tertiaryContainer = Color(0xFF534500)
)

// 2. Sage Green
private val SageGreenLight = lightColorScheme(
    primary = Color(0xFF2C6A4E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB4F1CE),
    onPrimaryContainer = Color(0xFF002114),
    secondary = Color(0xFF4F6354),
    secondaryContainer = Color(0xFFD2E8D5),
    tertiary = Color(0xFF3A656F),
    tertiaryContainer = Color(0xFFBFEAF7)
)
private val SageGreenDark = darkColorScheme(
    primary = Color(0xFF99D5B3),
    onPrimary = Color(0xFF003824),
    primaryContainer = Color(0xFF005234),
    secondary = Color(0xFFB6CCB9),
    secondaryContainer = Color(0xFF374B3D),
    tertiary = Color(0xFFA3CEDA),
    tertiaryContainer = Color(0xFF204D57)
)

// 3. Ocean Breeze
private val OceanBreezeLight = lightColorScheme(
    primary = Color(0xFF14607D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBCE9FF),
    onPrimaryContainer = Color(0xFF001F2A),
    secondary = Color(0xFF4B616D),
    secondaryContainer = Color(0xFFCEE6F3),
    tertiary = Color(0xFF5B5779),
    tertiaryContainer = Color(0xFFE1DFFF)
)
private val OceanBreezeDark = darkColorScheme(
    primary = Color(0xFF87CEF0),
    onPrimary = Color(0xFF00344A),
    primaryContainer = Color(0xFF004C69),
    secondary = Color(0xFFB3CAD7),
    secondaryContainer = Color(0xFF334A56),
    tertiary = Color(0xFFC5C3EA),
    tertiaryContainer = Color(0xFF434059)
)

// 4. Lavender Mist
private val LavenderMistLight = lightColorScheme(
    primary = Color(0xFF6B4EA8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEBDDFF),
    onPrimaryContainer = Color(0xFF230964),
    secondary = Color(0xFF615B71),
    secondaryContainer = Color(0xFFE7DFF8),
    tertiary = Color(0xFF7D5262),
    tertiaryContainer = Color(0xFFFFD8E4)
)
private val LavenderMistDark = darkColorScheme(
    primary = Color(0xFFD2BAFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF523691),
    secondary = Color(0xFFCCC2DC),
    secondaryContainer = Color(0xFF494352),
    tertiary = Color(0xFFEFB8C9),
    tertiaryContainer = Color(0xFF633B4A)
)

// 5. Warm Amber
private val WarmAmberLight = lightColorScheme(
    primary = Color(0xFF7A5900),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDEA6),
    onPrimaryContainer = Color(0xFF261A00),
    secondary = Color(0xFF6C5D47),
    secondaryContainer = Color(0xFFF6E0BB),
    tertiary = Color(0xFF48614A),
    tertiaryContainer = Color(0xFFC9E7C9)
)
private val WarmAmberDark = darkColorScheme(
    primary = Color(0xFFF5BE48),
    onPrimary = Color(0xFF412D00),
    primaryContainer = Color(0xFF5D4200),
    secondary = Color(0xFFD9C4A0),
    secondaryContainer = Color(0xFF53452F),
    tertiary = Color(0xFFADCBAE),
    tertiaryContainer = Color(0xFF324A35)
)

// 6. Midnight Ember (new)
private val MidnightEmberLight = lightColorScheme(
    primary = Color(0xFFB3321F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBD2),
    onPrimaryContainer = Color(0xFF410100),
    secondary = Color(0xFF77574C),
    secondaryContainer = Color(0xFFFFDBD2),
    tertiary = Color(0xFF6C5D2E),
    tertiaryContainer = Color(0xFFF8E0A0)
)
private val MidnightEmberDark = darkColorScheme(
    primary = Color(0xFFFFB4A2),
    onPrimary = Color(0xFF690100),
    primaryContainer = Color(0xFF932110),
    secondary = Color(0xFFE7BDB1),
    secondaryContainer = Color(0xFF5D4037),
    tertiary = Color(0xFFDBC48C),
    tertiaryContainer = Color(0xFF534500)
)

private val DarkColorScheme = AuroraDark
private val LightColorScheme = AuroraLight

/** Names shown in the colour picker, index-aligned with [palette]. */
val paletteNames = listOf(
    "Aurora", "Sunset Peach", "Sage Green", "Ocean Breeze", "Lavender Mist", "Warm Amber", "Midnight Ember"
)

private fun paletteScheme(dark: Boolean, index: Int) = when (index) {
    1 -> if (dark) SunsetPeachDark else SunsetPeachLight
    2 -> if (dark) SageGreenDark else SageGreenLight
    3 -> if (dark) OceanBreezeDark else OceanBreezeLight
    4 -> if (dark) LavenderMistDark else LavenderMistLight
    5 -> if (dark) WarmAmberDark else WarmAmberLight
    6 -> if (dark) MidnightEmberDark else MidnightEmberLight
    else -> if (dark) DarkColorScheme else LightColorScheme
}

/* ============================================================
   3. GLASS TOKENS
   Everything the glass components need, resolved once per theme.
   ============================================================ */

@androidx.compose.runtime.Immutable
data class GlassTokens(
    val isDark: Boolean,
    /** Translucent fill laid over the pane. */
    val fill: Color,
    /** Slightly stronger fill for "solid" glass like the mini player. */
    val fillStrong: Color,
    /** Top-left specular sheen. */
    val specular: Color,
    /** Rim / border gradient endpoints. */
    val rimStart: Color,
    val rimEnd: Color,
    /** Inner shadow that carves the pane. */
    val innerShadow: Color,
    /** Content colour for text placed on glass. */
    val content: Color,
    val contentMuted: Color,
    /** Hairline used for separators on glass. */
    val hairline: Color,
    /** Background gradient mesh stops. */
    val meshA: Color,
    val meshB: Color,
    val meshC: Color,
    val meshD: Color
)

private val DarkGlass = GlassTokens(
    isDark = true,
    fill = Color(0x1FFFFFFF),
    fillStrong = Color(0x2EFFFFFF),
    specular = SpecularDark,
    rimStart = Color(0x59FFFFFF),
    rimEnd = Color(0x14FFFFFF),
    innerShadow = GlassInnerShadowDark,
    content = Color(0xFFF6F6FB),
    contentMuted = Color(0xB3F6F6FB),
    hairline = Color(0x1FFFFFFF),
    meshA = Color(0xFF2B1B5A),
    meshB = Color(0xFF0B3A54),
    meshC = Color(0xFF4A1338),
    meshD = Color(0xFF0B4F3E)
)

private val LightGlass = GlassTokens(
    isDark = false,
    fill = Color(0x33FFFFFF),
    fillStrong = Color(0x59FFFFFF),
    specular = SpecularLight,
    rimStart = Color(0xB3FFFFFF),
    rimEnd = Color(0x4DFFFFFF),
    innerShadow = GlassInnerShadowLight,
    content = Color(0xFF14121C),
    contentMuted = Color(0xB314121C),
    hairline = Color(0x1F000000),
    meshA = Color(0xFFEDE7FF),
    meshB = Color(0xFFDFF3FF),
    meshC = Color(0xFFFFE7F2),
    meshD = Color(0xFFE0FBF1)
)

val LocalGlassTokens = staticCompositionLocalOf { DarkGlass }

@Composable
fun glassTokens(isDark: Boolean = LocalGlassTokens.current.isDark): GlassTokens =
    if (isDark) DarkGlass else LightGlass

/**
 * Legacy helper kept for the controls-colour customisation screens.
 */
@Composable
fun getControlsPrimaryColor(
    useCustomControlsColor: Boolean,
    controlsColorPalette: Int,
    darkTheme: Boolean = isSystemInDarkTheme()
): Color {
    if (!useCustomControlsColor) return MaterialTheme.colorScheme.onSurface
    if (controlsColorPalette == 0) return MaterialTheme.colorScheme.primary
    val palette = paletteScheme(darkTheme, controlsColorPalette)
    return if (darkTheme) palette.primary else palette.primary
}

/* ============================================================
   4. THEME
   ============================================================ */

@Composable
fun LuneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Off by default: wallpaper-derived colour replaced the whole Aurora
    // scheme on Android 12+, which left the glass surfaces tinted by whatever
    // the user's wallpaper happened to be instead of the app's brand.
    dynamicColor: Boolean = false,
    useCustomColors: Boolean = false,
    customColorPalette: Int = 0,
    useAmoledPitchBlack: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseColorScheme = when {
        useCustomColors -> paletteScheme(darkTheme, customColorPalette)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val colorScheme = if (darkTheme && useAmoledPitchBlack) {
        baseColorScheme.copy(
            background = Color.Black,
            surface = Color.Black,
            surfaceContainerLowest = Color.Black,
            surfaceContainerLow = Color(0xFF0A0A0A),
            surfaceContainer = Color(0xFF0C0C0C),
            surfaceContainerHigh = Color(0xFF121212),
            surfaceContainerHighest = Color(0xFF161616),
            surfaceVariant = Color(0xFF161616),
            secondaryContainer = Color(0xFF121212),
            tertiaryContainer = Color(0xFF141414)
        )
    } else {
        baseColorScheme
    }

    // Keep icon/status-bar contrast honest against whatever surface we land on.
    val forceLightIcons = remember(colorScheme) { colorScheme.surface.luminance() < 0.5f }

    CompositionLocalProvider(LocalGlassTokens provides if (darkTheme) DarkGlass else LightGlass) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !forceLightIcons
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !forceLightIcons
        }
    }
}
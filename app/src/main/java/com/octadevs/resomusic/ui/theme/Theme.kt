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

private val EmberDark = darkColorScheme(
    primary = EmberOrange,
    onPrimary = Color(0xFF1A0A02),
    primaryContainer = Color(0xFF5A2405),
    onPrimaryContainer = Color(0xFFFFDBC7),

    secondary = EmberAmber,
    onSecondary = Color(0xFF291800),
    secondaryContainer = Color(0xFF573100),
    onSecondaryContainer = Color(0xFFFFE0B2),

    tertiary = EmberGold,
    onTertiary = Color(0xFF2B1D00),
    tertiaryContainer = Color(0xFF5A4300),
    onTertiaryContainer = Color(0xFFFFE9A8),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = NightDeep,
    onBackground = Color(0xFFF5EEE8),
    surface = NightDeep,
    onSurface = Color(0xFFF5EEE8),
    surfaceVariant = NightElevated,
    onSurfaceVariant = Color(0xFFB5A79C),
    surfaceTint = EmberOrange,

    inverseSurface = Color(0xFFF5EEE8),
    inverseOnSurface = Color(0xFF241A14),
    inversePrimary = EmberAmber,

    surfaceDim = NightAbyss,
    surfaceBright = NightHigh,
    surfaceContainerLowest = NightAbyss,
    surfaceContainerLow = NightBase,
    surfaceContainer = NightBase,
    surfaceContainerHigh = NightElevated,
    surfaceContainerHighest = NightHigh,

    outline = Color(0xFF574538),
    outlineVariant = NightOutline,
    scrim = Color(0xFF000000)
)

private val EmberLight = lightColorScheme(
    primary = EmberOrangeDeep,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBC7),
    onPrimaryContainer = Color(0xFF3B1200),

    secondary = Color(0xFF8A5200),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFF2C1700),

    tertiary = Color(0xFF6B4E00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE9A8),
    onTertiaryContainer = Color(0xFF221700),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = DayBase,
    onBackground = Color(0xFF1F1611),
    surface = DayBase,
    onSurface = Color(0xFF1F1611),
    surfaceVariant = DayHigh,
    onSurfaceVariant = Color(0xFF5F564E),
    surfaceTint = EmberOrangeDeep,

    inverseSurface = Color(0xFF342A24),
    inverseOnSurface = Color(0xFFF9EFE7),
    inversePrimary = EmberAmber,

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

private val DarkColorScheme = EmberDark
private val LightColorScheme = EmberLight

/** Names shown in the colour picker, index-aligned with [palette]. */
val paletteNames = listOf(
    "Reso Ember", "Sunset Peach", "Sage Green", "Ocean Breeze", "Lavender Mist", "Warm Amber", "Midnight Ember"
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
    content = Color(0xFFF7F1EB),
    contentMuted = Color(0xB3F7F1EB),
    hairline = Color(0x1FFFFFFF),
    meshA = Color(0xFF4A1E06),
    meshB = Color(0xFF6B3505),
    meshC = Color(0xFF7A4404),
    meshD = Color(0xFF3A1A08)
)

private val LightGlass = GlassTokens(
    isDark = false,
    fill = Color(0x33FFFFFF),
    fillStrong = Color(0x59FFFFFF),
    specular = SpecularLight,
    rimStart = Color(0xB3FFFFFF),
    rimEnd = Color(0x4DFFFFFF),
    innerShadow = GlassInnerShadowLight,
    content = Color(0xFF1C1410),
    contentMuted = Color(0xB31C1410),
    hairline = Color(0x1F000000),
    meshA = Color(0xFFFFE8D2),
    meshB = Color(0xFFFFF2D8),
    meshC = Color(0xFFFFE0C2),
    meshD = Color(0xFFFFF8E6)
)

val LocalGlassTokens = staticCompositionLocalOf { DarkGlass }

/* ============================================================
   3b. USER GLASS CONTROLS
   ------------------------------------------------------------
   Live-tuned from the Liquid Glass settings screen. These are
   multipliers, not absolute colours, so every glass surface in the
   app re-resolves from one place and the sliders stay meaningful.
   Clamped in Glass.kt so no combination can destroy readability.
   ============================================================ */

@androidx.compose.runtime.Immutable
data class GlassUserTuning(
    /** 0..1 master multiplier on every glass alpha. */
    val intensity: Float = 1f,
    /** 0..1 rim / border strength. */
    val borderOpacity: Float = 1f,
    /** 0..1 drop-shadow + inner-shadow depth. */
    val shadowIntensity: Float = 1f,
    /** 0..1 warm accent bloom behind players. */
    val glowIntensity: Float = 0.55f,
    /** 0..1 translucency — higher means the pane shows more through. */
    val transparency: Float = 0.5f
) {
    /** Keeps sliders from producing unreadable or fully-opaque surfaces. */
    fun clamped() = GlassUserTuning(
        intensity = intensity.coerceIn(0.35f, 1.6f),
        borderOpacity = borderOpacity.coerceIn(0.2f, 1.5f),
        shadowIntensity = shadowIntensity.coerceIn(0f, 1.5f),
        glowIntensity = glowIntensity.coerceIn(0f, 1f),
        transparency = transparency.coerceIn(0.15f, 0.9f)
    )
}

val LocalGlassUserTuning = staticCompositionLocalOf { GlassUserTuning() }

val LocalGlowTokens = staticCompositionLocalOf {
    GlowTokens(
        primary = EmberOrange,
        secondary = EmberAmber,
        tertiary = EmberGold,
        backdrop = NightAbyss
    )
}

@androidx.compose.runtime.Immutable
data class GlowTokens(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val backdrop: Color
) {
    /** The ramp used for ambient bloom; ordered warm → bright. */
    val ramp: List<Color> get() = listOf(primary, secondary, tertiary)
}

@Composable
fun glowTokens(): GlowTokens = LocalGlowTokens.current

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
    // Off by default: wallpaper-derived colour replaced the whole scheme on
    // Android 12+, which left glass surfaces tinted by the user's wallpaper
    // instead of the app's brand.
    dynamicColor: Boolean = false,
    useCustomColors: Boolean = false,
    customColorPalette: Int = 0,
    useAmoledPitchBlack: Boolean = false,
    glassTuning: GlassUserTuning = GlassUserTuning(),
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

    CompositionLocalProvider(
        LocalGlassTokens provides if (darkTheme) DarkGlass else LightGlass,
        LocalGlassUserTuning provides glassTuning.clamped(),
        LocalGlowTokens provides GlowTokens(
            primary = colorScheme.primary,
            secondary = colorScheme.secondary,
            tertiary = colorScheme.tertiary,
            backdrop = colorScheme.background
        )
    ) {
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
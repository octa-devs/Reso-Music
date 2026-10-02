package com.octadevs.resomusic.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
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
import com.octadevs.resomusic.tools.SettingsManager
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/* ============================================================
   1. SIGNATURE SCHEME — "Noir"
   ------------------------------------------------------------
   A monochrome black field with exactly one chromatic voice: the
   sampled the logo's own ember ramp. Surfaces are near-black
   purple is reserved for glass fills, active states and progress,
   so it reads as light rather than as a colour scheme.
   ============================================================ */

private val NoirDark = darkColorScheme(
    primary = EmberOrange,
    onPrimary = Color(0xFF170C02),
    primaryContainer = Color(0xFF4A2408),
    onPrimaryContainer = Color(0xFFFFE2C4),

    secondary = EmberGold,
    onSecondary = Color(0xFF1A1002),
    secondaryContainer = Color(0xFF57380A),
    onSecondaryContainer = Color(0xFFFFEBC4),

    tertiary = EmberOrangeDeep,
    onTertiary = Color(0xFF180A01),
    tertiaryContainer = Color(0xFF452207),
    onTertiaryContainer = Color(0xFFFFDDB8),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = NoirAbyss,
    onBackground = Color(0xFFF6F1EC),
    surface = NoirAbyss,
    onSurface = Color(0xFFF6F1EC),
    surfaceVariant = NoirElevated,
    onSurfaceVariant = Color(0xFFB5ADA4),
    surfaceTint = EmberOrange,

    inverseSurface = Color(0xFFF6F1EC),
    inverseOnSurface = Color(0xFF171310),
    inversePrimary = EmberAmber,

    surfaceDim = NoirDeep,
    surfaceBright = NoirHigh,
    surfaceContainerLowest = NoirAbyss,
    surfaceContainerLow = NoirDeep,
    surfaceContainer = NoirBase,
    surfaceContainerHigh = NoirElevated,
    surfaceContainerHighest = NoirHigh,

    outline = Color(0xFF4F4841),
    outlineVariant = NoirOutline,
    scrim = Color(0xFF000000)
)

private val NoirLight = lightColorScheme(
    // Ember needs a much darker step in light mode: #FF6B1A on white is
    // only ~2.5:1, well under the 4.5:1 floor for body text. This sits at
    // ~5.1:1 while still reading as the same brand hue.
    primary = Color(0xFFA84300),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDCC2),
    onPrimaryContainer = Color(0xFF340F00),

    secondary = Color(0xFF7A5300),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDEA6),
    onSecondaryContainer = Color(0xFF271900),

    tertiary = Color(0xFF8A3410),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDBCB),
    onTertiaryContainer = Color(0xFF331100),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = DayBase,
    onBackground = Color(0xFF1A1512),
    surface = DayBase,
    onSurface = Color(0xFF1A1512),
    surfaceVariant = DayHigh,
    onSurfaceVariant = Color(0xFF625A53),
    surfaceTint = Color(0xFFA84300),

    inverseSurface = Color(0xFF2F2823),
    inverseOnSurface = Color(0xFFF7EFE8),
    inversePrimary = EmberOrange,

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

// 4. Soft Sand -- was "Lavender Mist". Retinted warm: purple is not part of
// the ResoMusic brand, and leaving it selectable meant the app could still
// render violet glass no matter what the default scheme said.
private val SoftSandLight = lightColorScheme(
    primary = Color(0xFF9A4A16),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBC6),
    onPrimaryContainer = Color(0xFF331000),
    secondary = Color(0xFF6F5B4B),
    secondaryContainer = Color(0xFFF6DCC8),
    tertiary = Color(0xFF7D5A2E),
    tertiaryContainer = Color(0xFFFFDDB0)
)
private val SoftSandDark = darkColorScheme(
    primary = Color(0xFFFFB68A),
    onPrimary = Color(0xFF542100),
    primaryContainer = Color(0xFF753100),
    secondary = Color(0xFFD8C2B0),
    secondaryContainer = Color(0xFF50453A),
    tertiary = Color(0xFFE8BE8C),
    tertiaryContainer = Color(0xFF5E3E17)
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

private val DarkColorScheme = NoirDark
private val LightColorScheme = NoirLight

/**
 * Turns Material's opaque container roles into translucent glass veils.
 *
 * This is the single highest-leverage lever for glassmorphism in a
 * Material3 app, and it is applied here rather than at 90 call sites on
 * purpose.
 *
 * Every "neutral" surface role in M3 -- `surfaceContainerLow` through
 * `surfaceContainerHighest`, plus `surfaceVariant` and `surfaceBright` -- is
 * defined as an *opaque* colour. That single fact is what keeps an app from
 * looking glassy no matter how much you polish the cards: M3's own components
 * (AlertDialog, ModalBottomSheet, ListItem, FilterChip, Menu, TextField) all
 * paint their chrome from those roles, so every one of them renders as an
 * opaque slab no matter what you do to your own components.
 *
 * Giving the roles an alpha makes the backdrop visible through them, and
 * because the same roles feed M3's built-in components, those become glass
 * for free -- no rewriting 30 dialog and sheet call sites, and no risk of
 * one of them being missed later.
 *
 * The veil is a light neutral in both themes, which is what real frosted
 * glass does: it scatters *light*, it does not tint. Tinting the pane with
 * the brand hue is handled separately by the glass tokens (`meshFill`,
 * `tintAlpha`), so hue stays adjustable in Liquid Glass settings without
 * this function needing to know about the palette at all.
 *
 * `surfaceContainerLowest` is deliberately left opaque. It is the floor a
 * sheet or dialog is laid over, and making it see-through would let the
 * content behind bleed through the surface it is supposed to be separating.
 * That is not glassmorphism, that is a rendering bug.
 *
 * @param isDark selects the alpha ladder. Dark glass needs a stronger veil to
 *        register at all against a near-black backdrop; light glass goes milky
 *        fast, so its rungs sit much higher and closer together.
 */
private fun ColorScheme.glassify(): ColorScheme {
    // Four rungs of the ladder, low -> highest.
    val low: Float
    val mid: Float
    val high: Float
    val top: Float

    if (isDarkScheme()) {
        // Kept low: these sit over a backdrop that is already almost black, so
        // even 17% reads as a clearly separate pane.
        low = GlassVeilLow
        mid = GlassVeilMid
        high = GlassVeilHigh
        top = GlassVeilTop
    } else {
        // Much heavier, because on a light backdrop a thin veil is invisible
        // and a thick one goes milky. Deliberately compressed to keep the
        // panes distinguishable without any reading as solid card stock.
        low = 0.62f
        mid = 0.70f
        high = 0.80f
        top = 0.88f
    }

    return copy(
        surfaceContainerLow = Color.White.copy(alpha = low),
        surfaceContainer = Color.White.copy(alpha = mid),
        surfaceContainerHigh = Color.White.copy(alpha = high),
        surfaceContainerHighest = Color.White.copy(alpha = top),
        surfaceVariant = Color.White.copy(alpha = high),
        surfaceBright = Color.White.copy(alpha = top)
    )
}

/** True when this scheme is one of the dark variants. */
private fun ColorScheme.isDarkScheme(): Boolean = luminance() < 0.5f

/* Veil rungs for dark mode. Named so the ladder reads as a scale rather than
   as four unexplained numbers. */
private const val GlassVeilLow = 0.05f
private const val GlassVeilMid = 0.08f
private const val GlassVeilHigh = 0.12f
private const val GlassVeilTop = 0.17f

/** Names shown in the colour picker, index-aligned with [palette]. */
val paletteNames = listOf(
    "Reso Noir", "Sunset Peach", "Sage Green", "Ocean Breeze", "Soft Sand", "Warm Amber", "Midnight Ember"
)

private fun paletteScheme(dark: Boolean, index: Int) = when (index) {
    1 -> if (dark) SunsetPeachDark else SunsetPeachLight
    2 -> if (dark) SageGreenDark else SageGreenLight
    3 -> if (dark) OceanBreezeDark else OceanBreezeLight
    4 -> if (dark) SoftSandDark else SoftSandLight
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
    /**
     * Purple wash layered over the neutral frosted base. Kept separate from
     * [fill] so the "Liquid Glass" saturation control can scale the colour
     * independently of how opaque the pane is.
     */
    val meshFill: Color,
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

/**
 * Dark glass is built the way the reference is built: a *neutral* frosted
 * base (white at low alpha, which picks up the blurred black backdrop and so
 * reads as grey) plus an ember wash on top. The wash is the logo's own
 * orange at ~0.42 alpha — the same visual weight the reference's purple pane
 * had, so glass surfaces keep the depth that was tuned against them.
 */
private val DarkGlass = GlassTokens(
    isDark = true,
    fill = Color(0x1AFFFFFF),
    fillStrong = Color(0x2BFFFFFF),
    /** Logo ember wash layered over the neutral base. */
    meshFill = Color(0x6BF0650F),
    specular = SpecularDark,
    rimStart = Color(0x4DFFFFFF),
    rimEnd = Color(0x0DFFFFFF),
    innerShadow = GlassInnerShadowDark,
    content = Color(0xFFF6F1EC),
    contentMuted = Color(0xB3F6F1EC),
    hairline = Color(0x1FFFFFFF),
    meshA = Color(0xFF2B1A0E),
    meshB = Color(0xFF3A2310),
    meshC = Color(0xFF1E1208),
    meshD = Color(0xFF0E0803)
)

private val LightGlass = GlassTokens(
    isDark = false,
    fill = Color(0x33FFFFFF),
    fillStrong = Color(0x59FFFFFF),
    meshFill = Color(0x4DA84300),
    specular = SpecularLight,
    rimStart = Color(0xB3FFFFFF),
    rimEnd = Color(0x4DFFFFFF),
    innerShadow = GlassInnerShadowLight,
    content = Color(0xFF1A1512),
    contentMuted = Color(0xB31A1512),
    hairline = Color(0x1F000000),
    meshA = Color(0xFFEDE6F6),
    meshB = Color(0xFFF6EAF2),
    meshC = Color(0xFFE6E0F0),
    meshD = Color(0xFFFAF8FD)
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
    val transparency: Float = 0.5f,
    /**
     * 0..1 colour strength of the purple bleed through the glass. This is the
     * single control that separates "generic frosted white" from the reference
     * material, where colour clearly comes through the pane.
     */
    val saturation: Float = 1f
) {
    /** Keeps sliders from producing unreadable or fully-opaque surfaces. */
    fun clamped() = GlassUserTuning(
        intensity = intensity.coerceIn(0.35f, 1.6f),
        borderOpacity = borderOpacity.coerceIn(0.2f, 1.5f),
        shadowIntensity = shadowIntensity.coerceIn(0f, 1.5f),
        glowIntensity = glowIntensity.coerceIn(0f, 1f),
        transparency = transparency.coerceIn(0.15f, 0.9f),
        saturation = saturation.coerceIn(0f, 1.6f)
    )
}

val LocalGlassUserTuning = staticCompositionLocalOf { GlassUserTuning() }

val LocalGlowTokens = staticCompositionLocalOf {
    GlowTokens(
        primary = EmberOrange,
        secondary = EmberGold,
        tertiary = EmberShadow,
        backdrop = NoirAbyss
    )
}

@androidx.compose.runtime.Immutable
data class GlowTokens(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val backdrop: Color
) {
    /** The ramp used for ambient bloom; ordered dim → bright. */
    val ramp: List<Color> get() = listOf(tertiary, primary, secondary)
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

/**
 * Reads the persisted Liquid Glass knobs and re-resolves whenever one changes,
 * so moving a slider repaints every glass surface in the app immediately.
 * `SettingsManager` holds these as `mutableStateOf`, which is what makes the
 * app-wide repaint work without any manual plumbing.
 */
@Composable
fun rememberGlassUserTuning(): GlassUserTuning {
    val context = LocalContext.current
    val settingsManager = remember(context) {
        SettingsManager.getInstance(context.applicationContext)
    }
    // Read the state-backed properties directly so composition subscribes to
    // each knob instead of snapshotting them once.
    return remember(
        settingsManager.glassIntensity,
        settingsManager.glassTransparency,
        settingsManager.glassSaturation,
        settingsManager.glassBorderOpacity,
        settingsManager.glassShadowIntensity,
        settingsManager.glassGlowIntensity
    ) {
        settingsManager.currentGlassTuning()
    }
}

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
    // `null` means "read the user's Liquid Glass preferences". Resolving here
    // rather than at each of the 16 call sites means the sliders can never be
    // half-wired, and a new screen picks the behaviour up for free.
    glassTuning: GlassUserTuning? = null,
    content: @Composable () -> Unit
) {
    val resolvedGlassTuning = glassTuning ?: rememberGlassUserTuning()

    val baseColorScheme = when {
        useCustomColors -> paletteScheme(darkTheme, customColorPalette)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // AMOLED is an explicit opt-out from glassmorphism, not an oversight. The
    // user asking for pitch black is asking for *no* lit panes, and a white
    // veil over pure black is just grey -- it would undo the one thing that
    // setting exists to do. So the opaque black ladder is kept verbatim there,
    // and every other configuration gets the translucent container roles.
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
        baseColorScheme.glassify()
    }

    // Keep icon/status-bar contrast honest against whatever surface we land on.
    val forceLightIcons = remember(colorScheme) { colorScheme.surface.luminance() < 0.5f }

    CompositionLocalProvider(
        LocalGlassTokens provides if (darkTheme) DarkGlass else LightGlass,
        LocalGlassUserTuning provides resolvedGlassTuning.clamped(),
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
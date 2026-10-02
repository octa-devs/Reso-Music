package com.octadevs.resomusic.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.octadevs.resomusic.BuildConfig
import com.octadevs.resomusic.R
import com.octadevs.resomusic.tools.MusicProvider
import com.octadevs.resomusic.tools.PlaylistBackupManager
import com.octadevs.resomusic.tools.PlaybackManager
import com.octadevs.resomusic.tools.SettingsManager
import com.octadevs.resomusic.ui.components.AppBlurBackdrop
import com.octadevs.resomusic.ui.components.BouncySwitch
import com.octadevs.resomusic.ui.components.GlassIconButton
import com.octadevs.resomusic.ui.components.LightSweep
import com.octadevs.resomusic.ui.components.StaggeredEntrance
import com.octadevs.resomusic.ui.components.glassPane
import com.octadevs.resomusic.ui.components.liquidGlass
import com.octadevs.resomusic.ui.theme.EmberAmber
import com.octadevs.resomusic.ui.theme.EmberOrange
import com.octadevs.resomusic.ui.theme.EmberOrangeDeep
import com.octadevs.resomusic.ui.theme.LuneTheme
import com.octadevs.resomusic.ui.theme.LocalGlassTokens
import com.octadevs.resomusic.ui.theme.MicroLabel
import com.octadevs.resomusic.ui.theme.Numeric
import com.octadevs.resomusic.ui.theme.paletteNames
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsManager = SettingsManager.getInstance(this)
            val systemInDarkTheme = isSystemInDarkTheme()
            val targetDarkTheme = when (settingsManager.themeMode) {
                1 -> false
                2 -> true
                else -> systemInDarkTheme
            }

            var useCustomColors by remember { mutableStateOf(settingsManager.useCustomColors) }
            var customColorPalette by remember { mutableIntStateOf(settingsManager.customColorPalette) }
            var useAmoledPitchBlack by remember { mutableStateOf(settingsManager.useAmoledPitchBlack) }

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                    if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                        useCustomColors = settingsManager.useCustomColors
                        customColorPalette = settingsManager.customColorPalette
                        useAmoledPitchBlack = settingsManager.useAmoledPitchBlack
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }

            LuneTheme(
                darkTheme = targetDarkTheme,
                useCustomColors = useCustomColors,
                customColorPalette = customColorPalette,
                useAmoledPitchBlack = useAmoledPitchBlack
            ) {
                SettingsScreen(onBack = { finish() })
            }
        }
    }
}

/* ============================================================================
   SETTINGS SCREEN
   ============================================================================ */

private val LANGUAGE_CODES = listOf("system", "en", "es", "pt-BR", "fr", "zh", "de", "ru", "fa", "ar")

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager.getInstance(context) }
    val scope = rememberCoroutineScope()
    val tokens = LocalGlassTokens.current

    val playbackManager = remember { PlaybackManager.getInstance(context) }
    val currentSong = playbackManager.currentSong

    /* ---- mirrors of persisted values ---- */
    var themeMode by remember { mutableIntStateOf(settingsManager.themeMode) }
    var useCustomColors by remember { mutableStateOf(settingsManager.useCustomColors) }
    var customColorPalette by remember { mutableIntStateOf(settingsManager.customColorPalette) }
    var useAmoledPitchBlack by remember { mutableStateOf(settingsManager.useAmoledPitchBlack) }

    var isBlurEnabled by remember { mutableStateOf(settingsManager.isBlurEnabled) }
    var isBlurDarkMode by remember { mutableStateOf(settingsManager.isBlurDarkMode) }
    var isBlurLightMode by remember { mutableStateOf(settingsManager.isBlurLightMode) }
    var isHeaderWaveEnabled by remember { mutableStateOf(settingsManager.isHeaderWaveEffectEnabled) }

    var isCinematicEnabled by remember { mutableStateOf(settingsManager.isCinematicPlayerEnabled) }
    var hapticEnabled by remember { mutableStateOf(settingsManager.isHapticVibrationEnabled) }
    var songInfoEnabled by remember { mutableStateOf(settingsManager.isSongInfoEnabled) }
    var bitrateOnList by remember { mutableStateOf(settingsManager.isBitrateOnList) }
    var bitrateOnPlayer by remember { mutableStateOf(settingsManager.isBitrateOnPlayer) }
    var optionsBarVisible by remember { mutableStateOf(settingsManager.isOptionsBarVisible) }
    var seamlessLooping by remember { mutableStateOf(settingsManager.seamlessLooping) }
    var keepScreenOn by remember { mutableStateOf(settingsManager.keepScreenOn) }

    var tracksViewStyle by remember { mutableIntStateOf(settingsManager.tracksViewStyle) }
    var showAllFolders by remember { mutableStateOf(settingsManager.showAllFoldersOnStart) }
    var showWhatsapp by remember { mutableStateOf(settingsManager.showWhatsappAudio) }

    var showBackupWarning by remember { mutableStateOf(settingsManager.showBackupWarning) }
    var autoSyncBackupUri by remember { mutableStateOf(settingsManager.autoSyncBackupUri) }
    var isAutoSyncEnabled by remember { mutableStateOf(settingsManager.isAutoSyncBackupEnabled) }
    var isScanningLibrary by remember { mutableStateOf(false) }

    var showThemeDialog by remember { mutableStateOf(false) }
    var showPaletteDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showViewStyleDialog by remember { mutableStateOf(false) }

    val currentLanguage = settingsManager.language
    val backupManager = remember { PlaylistBackupManager(context) }
    val musicProvider = remember { MusicProvider(context) }

    val isSystemDark = isSystemInDarkTheme()
    val isDarkTheme = when (themeMode) {
        1 -> false
        2 -> true
        else -> isSystemDark
    }
    val hasBlurBackground = isBlurEnabled &&
        ((isDarkTheme && isBlurDarkMode) || (!isDarkTheme && isBlurLightMode))

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            settingsManager.autoSyncBackupUri = it.toString()
            autoSyncBackupUri = it.toString()
            scope.launch {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    val success = backupManager.exportPlaylists(outputStream)
                    Toast.makeText(
                        context,
                        if (success) context.getString(R.string.export_success)
                        else context.getString(R.string.export_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val success = backupManager.importPlaylists(inputStream)
                    Toast.makeText(
                        context,
                        if (success) context.getString(R.string.import_success)
                        else context.getString(R.string.import_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /* ---------- Dialogs ---------- */
    if (showThemeDialog) {
        SettingsChoiceDialog(
            title = stringResource(R.string.theme),
            options = listOf(
                stringResource(R.string.theme_auto) to Icons.Default.BrightnessAuto,
                stringResource(R.string.theme_light) to Icons.Default.LightMode,
                stringResource(R.string.theme_dark) to Icons.Default.DarkMode
            ),
            selectedIndex = themeMode,
            onSelect = { index ->
                themeMode = index
                settingsManager.themeMode = index
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showPaletteDialog) {
        SettingsPaletteDialog(
            selected = customColorPalette,
            onSelect = { index ->
                customColorPalette = index
                settingsManager.customColorPalette = index
                showPaletteDialog = false
            },
            onDismiss = { showPaletteDialog = false }
        )
    }

    if (showViewStyleDialog) {
        SettingsChoiceDialog(
            title = stringResource(R.string.tracks_view),
            options = listOf(
                stringResource(R.string.view_list) to Icons.Default.ViewList,
                stringResource(R.string.view_grid) to Icons.Default.GridView
            ),
            selectedIndex = tracksViewStyle,
            onSelect = { index ->
                tracksViewStyle = index
                settingsManager.tracksViewStyle = index
                showViewStyleDialog = false
            },
            onDismiss = { showViewStyleDialog = false }
        )
    }

    if (showLanguageDialog) {
        SettingsChoiceDialog(
            title = stringResource(R.string.select_language),
            options = listOf(
                stringResource(R.string.lang_system),
                stringResource(R.string.lang_english),
                stringResource(R.string.lang_spanish),
                stringResource(R.string.lang_portuguese),
                stringResource(R.string.lang_french),
                stringResource(R.string.lang_chinese),
                stringResource(R.string.lang_german),
                stringResource(R.string.lang_russian),
                stringResource(R.string.lang_persian),
                stringResource(R.string.lang_arabic)
            ).map { it to null },
            selectedIndex = LANGUAGE_CODES.indexOf(currentLanguage).coerceAtLeast(0),
            onSelect = { index ->
                val code = LANGUAGE_CODES[index]
                settingsManager.language = code
                AppCompatDelegate.setApplicationLocales(
                    if (code == "system") LocaleListCompat.getEmptyLocaleList()
                    else LocaleListCompat.forLanguageTags(code)
                )
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    /* ---------- Screen ---------- */
    AppBlurBackdrop(
        hasBlurBackground = hasBlurBackground,
        isDarkTheme = isDarkTheme,
        currentSong = currentSong
    ) {
        val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            item(key = "hero") {
                SettingsHero(
                    title = stringResource(R.string.settings),
                    subtitle = stringResource(R.string.settings_subtitle),
                    version = BuildConfig.VERSION_NAME,
                    onBack = onBack
                )
            }

            item(key = "appearance") {
                StaggeredEntrance(index = 0) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        SettingsSection(title = stringResource(R.string.appearance)) {
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.theme),
                                supportingText = when (themeMode) {
                                    1 -> stringResource(R.string.theme_light)
                                    2 -> stringResource(R.string.theme_dark)
                                    else -> stringResource(R.string.theme_auto)
                                },
                                icon = Icons.Default.LightMode,
                                position = SectionPosition.FIRST,
                                onClick = { showThemeDialog = true }
                            )

                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.use_custom_colors),
                                supportingText = stringResource(R.string.use_custom_colors_desc),
                                icon = Icons.Default.Palette,
                                position = SectionPosition.MIDDLE,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = useCustomColors,
                                        onCheckedChange = {
                                            useCustomColors = it
                                            settingsManager.useCustomColors = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (useCustomColors) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )

                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.color_palette),
                                supportingText = if (useCustomColors) {
                                    paletteNames.getOrElse(customColorPalette) { paletteNames[0] }
                                } else {
                                    stringResource(R.string.color_palette_locked)
                                },
                                icon = Icons.Default.ColorLens,
                                position = if (useCustomColors) SectionPosition.MIDDLE else SectionPosition.LAST,
                                onClick = { if (useCustomColors) showPaletteDialog = true }
                            )

                            if (useCustomColors) {
                                SettingsPreferenceItem(
                                    headlineText = stringResource(R.string.amoled_pitch_black),
                                    supportingText = stringResource(R.string.amoled_pitch_black_desc),
                                    icon = Icons.Default.BrightnessLow,
                                    position = SectionPosition.LAST,
                                    trailingContent = {
                                        BouncySwitch(
                                            checked = useAmoledPitchBlack,
                                            onCheckedChange = {
                                                useAmoledPitchBlack = it
                                                settingsManager.useAmoledPitchBlack = it
                                            },
                                            thumbContent = {
                                                Icon(
                                                    imageVector = if (useAmoledPitchBlack) Icons.Default.Check else Icons.Default.Close,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item(key = "glass") {
                StaggeredEntrance(index = 1) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        SettingsSection(title = stringResource(R.string.liquid_glass)) {
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.enable_blur),
                                supportingText = stringResource(R.string.enable_blur_desc),
                                icon = Icons.Default.Waves,
                                position = if (isBlurEnabled) SectionPosition.FIRST else SectionPosition.SINGLE,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = isBlurEnabled,
                                        onCheckedChange = {
                                            isBlurEnabled = it
                                            settingsManager.isBlurEnabled = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (isBlurEnabled) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )

                            if (isBlurEnabled) {
                                SettingsPreferenceItem(
                                    headlineText = stringResource(R.string.blur_dark_mode),
                                    supportingText = stringResource(R.string.blur_dark_mode_desc),
                                    icon = Icons.Default.DarkMode,
                                    position = SectionPosition.MIDDLE,
                                    trailingContent = {
                                        BouncySwitch(
                                            checked = isBlurDarkMode,
                                            onCheckedChange = {
                                                isBlurDarkMode = it
                                                settingsManager.isBlurDarkMode = it
                                            },
                                            thumbContent = {
                                                Icon(
                                                    imageVector = if (isBlurDarkMode) Icons.Default.Check else Icons.Default.Close,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                                )
                                            }
                                        )
                                    }
                                )
                                SettingsPreferenceItem(
                                    headlineText = stringResource(R.string.blur_light_mode),
                                    supportingText = stringResource(R.string.blur_light_mode_desc),
                                    icon = Icons.Default.LightMode,
                                    position = SectionPosition.MIDDLE,
                                    trailingContent = {
                                        BouncySwitch(
                                            checked = isBlurLightMode,
                                            onCheckedChange = {
                                                isBlurLightMode = it
                                                settingsManager.isBlurLightMode = it
                                            },
                                            thumbContent = {
                                                Icon(
                                                    imageVector = if (isBlurLightMode) Icons.Default.Check else Icons.Default.Close,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                                )
                                            }
                                        )
                                    }
                                )
                                SettingsPreferenceItem(
                                    headlineText = stringResource(R.string.header_wave_effect),
                                    supportingText = stringResource(R.string.header_wave_effect_desc),
                                    icon = Icons.Default.Waves,
                                    position = SectionPosition.MIDDLE,
                                    trailingContent = {
                                        BouncySwitch(
                                            checked = isHeaderWaveEnabled,
                                            onCheckedChange = {
                                                isHeaderWaveEnabled = it
                                                settingsManager.isHeaderWaveEffectEnabled = it
                                            },
                                            thumbContent = {
                                                Icon(
                                                    imageVector = if (isHeaderWaveEnabled) Icons.Default.Check else Icons.Default.Close,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                                )
                                            }
                                        )
                                    }
                                )
                            }

                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.liquid_glass),
                                supportingText = stringResource(R.string.liquid_glass_desc),
                                icon = Icons.Default.LightMode,
                                position = SectionPosition.MIDDLE,
                                onClick = { context.startActivity(Intent(context, LiquidGlassActivity::class.java)) }
                            )

                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.advanced_glass),
                                supportingText = stringResource(R.string.advanced_glass_desc),
                                icon = Icons.Default.Tune,
                                position = SectionPosition.LAST,
                                onClick = { context.startActivity(Intent(context, BlurCustomizationActivity::class.java)) }
                            )
                        }
                    }
                }
            }

            item(key = "playback") {
                StaggeredEntrance(index = 2) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        SettingsSection(title = stringResource(R.string.playback)) {
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.audio_settings),
                                supportingText = stringResource(R.string.audio_settings_desc),
                                icon = Icons.Default.MusicNote,
                                position = SectionPosition.FIRST,
                                onClick = { context.startActivity(Intent(context, AudioSettingsActivity::class.java)) }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.eq_title),
                                supportingText = stringResource(R.string.eq_title_desc),
                                icon = Icons.Default.GraphicEq,
                                position = SectionPosition.MIDDLE,
                                onClick = { context.startActivity(Intent(context, EqualizerActivity::class.java)) }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.cinematic_player),
                                supportingText = stringResource(R.string.cinematic_player_desc),
                                icon = Icons.Default.AutoAwesome,
                                position = SectionPosition.MIDDLE,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = isCinematicEnabled,
                                        onCheckedChange = {
                                            isCinematicEnabled = it
                                            settingsManager.isCinematicPlayerEnabled = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (isCinematicEnabled) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.seamless_looping),
                                supportingText = stringResource(R.string.seamless_looping_desc),
                                icon = Icons.Default.Repeat,
                                position = SectionPosition.MIDDLE,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = seamlessLooping,
                                        onCheckedChange = {
                                            seamlessLooping = it
                                            settingsManager.seamlessLooping = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (seamlessLooping) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.keep_screen_on),
                                supportingText = stringResource(R.string.keep_screen_on_desc),
                                icon = Icons.Default.LightMode,
                                position = SectionPosition.MIDDLE,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = keepScreenOn,
                                        onCheckedChange = {
                                            keepScreenOn = it
                                            settingsManager.keepScreenOn = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (keepScreenOn) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.haptic_vibration),
                                supportingText = stringResource(R.string.haptic_vibration_desc),
                                icon = Icons.Default.Notifications,
                                position = SectionPosition.LAST,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = hapticEnabled,
                                        onCheckedChange = {
                                            hapticEnabled = it
                                            settingsManager.isHapticVibrationEnabled = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (hapticEnabled) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item(key = "interface") {
                StaggeredEntrance(index = 3) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        SettingsSection(title = stringResource(R.string.interface_section)) {
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.customization),
                                supportingText = stringResource(R.string.customization_desc),
                                icon = Icons.Default.Brush,
                                position = SectionPosition.FIRST,
                                onClick = { context.startActivity(Intent(context, CustomizationActivity::class.java)) }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.tracks_view),
                                supportingText = if (tracksViewStyle == 0) {
                                    stringResource(R.string.view_list)
                                } else {
                                    stringResource(R.string.view_grid)
                                },
                                icon = Icons.Default.GridView,
                                position = SectionPosition.MIDDLE,
                                onClick = { showViewStyleDialog = true }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.song_info),
                                supportingText = stringResource(R.string.song_info_desc),
                                icon = Icons.Default.Info,
                                position = SectionPosition.MIDDLE,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = songInfoEnabled,
                                        onCheckedChange = {
                                            songInfoEnabled = it
                                            settingsManager.isSongInfoEnabled = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (songInfoEnabled) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.show_in_song_list),
                                supportingText = stringResource(R.string.audio_bitrate),
                                icon = Icons.Default.MusicNote,
                                position = SectionPosition.MIDDLE,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = bitrateOnList,
                                        onCheckedChange = {
                                            bitrateOnList = it
                                            settingsManager.isBitrateOnList = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (bitrateOnList) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.show_in_full_player),
                                supportingText = stringResource(R.string.audio_sample_rate),
                                icon = Icons.Default.GraphicEq,
                                position = SectionPosition.MIDDLE,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = bitrateOnPlayer,
                                        onCheckedChange = {
                                            bitrateOnPlayer = it
                                            settingsManager.isBitrateOnPlayer = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (bitrateOnPlayer) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.show_options),
                                supportingText = stringResource(R.string.show_options_desc),
                                icon = Icons.Default.Tune,
                                position = SectionPosition.MIDDLE,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = optionsBarVisible,
                                        onCheckedChange = {
                                            optionsBarVisible = it
                                            settingsManager.isOptionsBarVisible = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (optionsBarVisible) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.language),
                                supportingText = when (currentLanguage) {
                                    "en" -> stringResource(R.string.lang_english)
                                    "es" -> stringResource(R.string.lang_spanish)
                                    "pt-BR" -> stringResource(R.string.lang_portuguese)
                                    "fr" -> stringResource(R.string.lang_french)
                                    "zh" -> stringResource(R.string.lang_chinese)
                                    "de" -> stringResource(R.string.lang_german)
                                    "ru" -> stringResource(R.string.lang_russian)
                                    "fa" -> stringResource(R.string.lang_persian)
                                    "ar" -> stringResource(R.string.lang_arabic)
                                    else -> stringResource(R.string.lang_system)
                                },
                                icon = Icons.Default.Language,
                                position = SectionPosition.LAST,
                                onClick = { showLanguageDialog = true }
                            )
                        }
                    }
                }
            }

            item(key = "library") {
                StaggeredEntrance(index = 4) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        SettingsSection(title = stringResource(R.string.library)) {
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.rescan_library),
                                supportingText = stringResource(R.string.rescan_library_desc),
                                icon = Icons.Default.Sync,
                                position = SectionPosition.FIRST,
                                trailingContent = if (isScanningLibrary) {
                                    {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else null,
                                onClick = {
                                    if (!isScanningLibrary) {
                                        scope.launch {
                                            isScanningLibrary = true
                                            val songs = musicProvider.refreshLibrary()
                                            isScanningLibrary = false
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.rescan_library_success, songs.size),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.section_customization),
                                supportingText = stringResource(R.string.section_customization_desc),
                                icon = Icons.Default.Album,
                                position = SectionPosition.MIDDLE,
                                onClick = { context.startActivity(Intent(context, CustomizationActivity::class.java)) }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.show_all_folders),
                                supportingText = stringResource(R.string.show_all_folders_desc),
                                icon = Icons.Default.Folder,
                                position = SectionPosition.MIDDLE,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = showAllFolders,
                                        onCheckedChange = {
                                            showAllFolders = it
                                            settingsManager.showAllFoldersOnStart = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (showAllFolders) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.whatsapp_audio),
                                supportingText = stringResource(R.string.whatsapp_audio_desc),
                                icon = Icons.Default.Folder,
                                position = SectionPosition.LAST,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = showWhatsapp,
                                        onCheckedChange = {
                                            showWhatsapp = it
                                            settingsManager.showWhatsappAudio = it
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (showWhatsapp) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item(key = "backup") {
                StaggeredEntrance(index = 5) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        SettingsSection(title = stringResource(R.string.backup)) {
                            if (showBackupWarning) {
                                SettingsInlineCard {
                                    BackupWarningCard(
                                        onDismiss = {
                                            showBackupWarning = false
                                            settingsManager.showBackupWarning = false
                                        }
                                    )
                                }
                            }
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.export_playlists),
                                supportingText = stringResource(R.string.export_playlists_desc),
                                icon = Icons.Default.CloudDownload,
                                position = SectionPosition.FIRST,
                                onClick = { exportLauncher.launch("playlists_backup.json") }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.import_playlists),
                                supportingText = stringResource(R.string.import_playlists_desc),
                                icon = Icons.Default.Refresh,
                                position = SectionPosition.MIDDLE,
                                onClick = {
                                    importLauncher.launch(
                                        arrayOf("application/json", "application/octet-stream")
                                    )
                                }
                            )
                            val hasExportDestination = !autoSyncBackupUri.isNullOrBlank()
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.auto_sync_backup),
                                supportingText = if (hasExportDestination) {
                                    stringResource(R.string.auto_sync_backup_desc)
                                } else {
                                    stringResource(R.string.auto_sync_requires_export)
                                },
                                icon = Icons.Default.Sync,
                                position = SectionPosition.LAST,
                                trailingContent = {
                                    BouncySwitch(
                                        checked = isAutoSyncEnabled && hasExportDestination,
                                        enabled = hasExportDestination,
                                        onCheckedChange = { enabled ->
                                            isAutoSyncEnabled = enabled
                                            settingsManager.isAutoSyncBackupEnabled = enabled
                                            if (enabled) backupManager.triggerAutoSync()
                                        },
                                        thumbContent = {
                                            Icon(
                                                imageVector = if (isAutoSyncEnabled && hasExportDestination) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize)
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item(key = "security") {
                StaggeredEntrance(index = 6) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        SettingsSection(title = stringResource(R.string.security)) {
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.permissions),
                                supportingText = stringResource(R.string.permissions_desc),
                                icon = Icons.Default.Security,
                                position = SectionPosition.FIRST,
                                onClick = { context.startActivity(Intent(context, PermissionsActivity::class.java)) }
                            )
                            SettingsPreferenceItem(
                                headlineText = stringResource(R.string.about),
                                supportingText = BuildConfig.VERSION_NAME,
                                icon = Icons.Default.Info,
                                position = SectionPosition.LAST,
                                onClick = { context.startActivity(Intent(context, AboutActivity::class.java)) }
                            )
                        }
                    }
                }
            }

            item(key = "footer") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 44.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.settings_footer).uppercase(),
                        style = MicroLabel,
                        color = tokens.contentMuted
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "RESO MUSIC  v${BuildConfig.VERSION_NAME}",
                        style = Numeric,
                        color = tokens.content.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

/* ============================================================================
   HERO HEADER
   ============================================================================ */

@Composable
private fun SettingsHero(
    title: String,
    subtitle: String,
    version: String,
    onBack: () -> Unit
) {
    val tokens = LocalGlassTokens.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .liquidGlass(
                    shape = RoundedCornerShape(32.dp),
                    cornerRadius = 32.dp,
                    strong = true,
                    raised = true
                )
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                EmberOrange.copy(alpha = 0.30f),
                                EmberAmber.copy(alpha = 0.14f),
                                EmberOrangeDeep.copy(alpha = 0.24f)
                            )
                        )
                    )
            )
            LightSweep(modifier = Modifier.matchParentSize())

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 22.dp, end = 22.dp, bottom = 22.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.app_name).uppercase(),
                        style = MicroLabel,
                        color = tokens.content.copy(alpha = 0.75f)
                    )
                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color.White.copy(alpha = 0.20f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "v$version",
                            style = MicroLabel,
                            color = tokens.content
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = title,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 46.sp,
                        lineHeight = 46.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1.8).sp,
                        brush = Brush.linearGradient(
                            listOf(Color.White, Color.White.copy(alpha = 0.70f))
                        )
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.content.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        GlassIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.cd_back),
            onClick = onBack,
            tint = tokens.content,
            strong = true,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
        )
    }
}

/* ============================================================================
   DIALOGS
   ============================================================================ */

@Composable
private fun SettingsChoiceDialog(
    title: String,
    options: List<Pair<String, ImageVector?>>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        // A floating pane, so the high glass rung. `tokens` is gone from here:
        // it existed only to pick between two hardcoded slab colours.
        containerColor = glassPane(0.62f),
        shape = RoundedCornerShape(30.dp),
        title = {
            Text(
                text = title.uppercase(),
                style = MicroLabel,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                Modifier
                    .heightIn(max = 380.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                options.forEachIndexed { index, option ->
                    val selected = index == selectedIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                                else Color.Transparent
                            )
                            .clickable { onSelect(index) }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        option.second?.let { iconVector ->
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(14.dp))
                        }
                        Text(
                            text = option.first,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        AnimatedVisibility(visible = selected, enter = fadeIn(), exit = fadeOut()) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
private fun SettingsPaletteDialog(
    selected: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = LocalGlassTokens.current.isDark
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = glassPane(0.62f),
        shape = RoundedCornerShape(30.dp),
        title = {
            Text(
                text = stringResource(R.string.color_palette).uppercase(),
                style = MicroLabel,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                paletteNames.forEachIndexed { index, name ->
                    val isSelected = index == selected
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.985f,
                        animationSpec = spring(),
                        label = "paletteScale"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scale)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                                else Color.Transparent
                            )
                            .clickable { onSelect(index) }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PaletteSwatch(index = index, isDark = isDark)
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
private fun PaletteSwatch(index: Int, isDark: Boolean) {
    val swatch = remember(index) {
        when (index) {
            1 -> listOf(Color(0xFFB04B38), Color(0xFFFFB4AA))
            2 -> listOf(Color(0xFF386B52), Color(0xFF9FD3B1))
            3 -> listOf(Color(0xFF2E6580), Color(0xFF99CCEA))
            4 -> listOf(Color(0xFF6E568F), Color(0xFFD6BAFF))
            5 -> listOf(Color(0xFF7F5700), Color(0xFFFCBC43))
            6 -> listOf(Color(0xFFB3321F), Color(0xFFFFB4A2))
            else -> listOf(Color(0xFF5B45C9), Color(0xFF22D3EE))
        }
    }
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(if (isDark) swatch.reversed() else swatch))
    )
}

/* ============================================================================
   SHARED SETTINGS PIECES  (also used by the customization screens)
   ============================================================================ */

enum class SectionPosition {
    FIRST, MIDDLE, LAST, SINGLE
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(Modifier.padding(bottom = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.uppercase(),
                style = MicroLabel,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.30f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        content()
    }
}

/** Lets non-row content (cards, notices) line up with the rows around it. */
@Composable
fun SettingsInlineCard(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) { content() }
}

@Composable
fun SettingsPreferenceItem(
    headlineText: String,
    supportingText: String? = null,
    icon: ImageVector,
    position: SectionPosition,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val tokens = LocalGlassTokens.current
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager.getInstance(context) }
    val isSystemDark = isSystemInDarkTheme()
    val isDarkTheme = when (settingsManager.themeMode) {
        1 -> false
        2 -> true
        else -> isSystemDark
    }
    val hasBlurBackground = settingsManager.isBlurEnabled &&
        ((isDarkTheme && settingsManager.isBlurDarkMode) || (!isDarkTheme && settingsManager.isBlurLightMode))

    val shape = when (position) {
        SectionPosition.FIRST -> RoundedCornerShape(
            topStart = 24.dp, topEnd = 24.dp, bottomStart = 6.dp, bottomEnd = 6.dp
        )
        SectionPosition.MIDDLE -> RoundedCornerShape(6.dp)
        SectionPosition.LAST -> RoundedCornerShape(
            topStart = 6.dp, topEnd = 6.dp, bottomStart = 24.dp, bottomEnd = 24.dp
        )
        SectionPosition.SINGLE -> RoundedCornerShape(24.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .liquidGlass(
                shape = shape,
                cornerRadius = 22.dp,
                strong = !hasBlurBackground,
                raised = true
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.26f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (tokens.isDark) Color.White else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = headlineText,
                style = MaterialTheme.typography.titleMedium,
                color = tokens.content
            )
            if (supportingText != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.contentMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (trailingContent != null) {
            Spacer(Modifier.width(12.dp))
            trailingContent()
        } else if (onClick != null) {
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = tokens.contentMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun BackupWarningCard(onDismiss: () -> Unit) {
    val tokens = LocalGlassTokens.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .liquidGlass(
                shape = RoundedCornerShape(24.dp),
                cornerRadius = 24.dp,
                strong = true,
                raised = true,
                tint = Color(0xFFFF8A5C)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = Color(0xFFFF9E6B),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.backup_warning_title),
                style = MaterialTheme.typography.titleMedium,
                color = tokens.content
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.backup_warning_desc),
                style = MaterialTheme.typography.bodySmall,
                color = tokens.contentMuted
            )
        }
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.16f))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = tokens.content,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
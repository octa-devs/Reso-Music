package com.octadevs.resomusic.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.octadevs.resomusic.R
import com.octadevs.resomusic.tools.SettingsManager
import com.octadevs.resomusic.ui.components.AmbientGlowBackground
import com.octadevs.resomusic.ui.components.BouncySwitch
import com.octadevs.resomusic.ui.components.GlassDivider
import com.octadevs.resomusic.ui.components.GlassSurface
import com.octadevs.resomusic.ui.theme.GlassUserTuning
import com.octadevs.resomusic.ui.theme.LocalGlassTokens
import com.octadevs.resomusic.ui.theme.LuneTheme
import com.octadevs.resomusic.ui.theme.MicroLabel
import com.octadevs.resomusic.ui.utils.bounceClick

/* ============================================================================
   LIQUID GLASS
   A dedicated screen for the appearance of every glass surface in the app.
   Sliders write straight to SettingsManager, which holds them in Compose
   state, so the preview card and the real UI update in the same frame.
   ============================================================================ */

class LiquidGlassActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsManager = SettingsManager.getInstance(this)
        enableEdgeToEdge()
        setContent {
            val targetDarkTheme = when (settingsManager.themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            LuneTheme(
                darkTheme = targetDarkTheme,
                useCustomColors = settingsManager.useCustomColors,
                customColorPalette = settingsManager.customColorPalette,
                useAmoledPitchBlack = settingsManager.useAmoledPitchBlack
            ) {
                LiquidGlassScreen(onBack = { finish() })
            }
        }
    }
}

/** Inclusive range for one slider row. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidGlassScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val settingsManager = remember { SettingsManager.getInstance(context) }

    // Mirrors of the persisted values. Writes go to both the mirror (so this
    // screen repaints) and SettingsManager (so it survives a restart and so
    // every other screen repaints).
    var intensity by remember { mutableFloatStateOf(settingsManager.glassIntensity) }
    var transparency by remember { mutableFloatStateOf(settingsManager.glassTransparency) }
    var border by remember { mutableFloatStateOf(settingsManager.glassBorderOpacity) }
    var shadow by remember { mutableFloatStateOf(settingsManager.glassShadowIntensity) }
    var glow by remember { mutableFloatStateOf(settingsManager.glassGlowIntensity) }
    var ambient by remember { mutableStateOf(settingsManager.glassAmbientEnabled) }
    var animations by remember { mutableStateOf(settingsManager.glassAnimationsEnabled) }

    val previewTuning = remember(intensity, transparency, border, shadow, glow) {
        GlassUserTuning(
            intensity = intensity,
            borderOpacity = border,
            shadowIntensity = shadow,
            glowIntensity = glow,
            transparency = transparency
        ).clamped()
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    AmbientGlowBackground(strength = if (ambient) 1f else 0f) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.liquid_glass),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.bounceClick()) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.cd_back),
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    stringResource(R.string.liquid_glass_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                GlassPreviewCard(
                    tuning = previewTuning,
                    glow = glow,
                    isPlaying = true
                )

                GlassKnobGroup(title = stringResource(R.string.glass_surface)) {
                    GlassSlider(
                        label = stringResource(R.string.glass_intensity),
                        value = intensity,
                        range = 0.35f..1.6f,
                        onValueChange = {
                            intensity = it
                            settingsManager.glassIntensity = it
                        }
                    )
                    GlassSlider(
                        label = stringResource(R.string.glass_transparency),
                        value = transparency,
                        range = 0.15f..0.9f,
                        onValueChange = {
                            transparency = it
                            settingsManager.glassTransparency = it
                        }
                    )
                }

                GlassKnobGroup(title = stringResource(R.string.glass_depth)) {
                    GlassSlider(
                        label = stringResource(R.string.glass_border_opacity),
                        value = border,
                        range = 0.2f..1.5f,
                        onValueChange = {
                            border = it
                            settingsManager.glassBorderOpacity = it
                        }
                    )
                    GlassSlider(
                        label = stringResource(R.string.glass_shadow_intensity),
                        value = shadow,
                        range = 0f..1.5f,
                        onValueChange = {
                            shadow = it
                            settingsManager.glassShadowIntensity = it
                        }
                    )
                    GlassSlider(
                        label = stringResource(R.string.glass_glow_intensity),
                        value = glow,
                        range = 0f..1f,
                        onValueChange = {
                            glow = it
                            settingsManager.glassGlowIntensity = it
                        }
                    )
                }

                GlassKnobGroup(title = stringResource(R.string.general)) {
                    GlassToggleRow(
                        label = stringResource(R.string.glass_ambient),
                        checked = ambient,
                        onCheckedChange = {
                            ambient = it
                            settingsManager.glassAmbientEnabled = it
                        }
                    )
                    GlassToggleRow(
                        label = stringResource(R.string.glass_animations),
                        checked = animations,
                        onCheckedChange = {
                            animations = it
                            settingsManager.glassAnimationsEnabled = it
                        }
                    )
                }

                GlassResetButton(
                    onReset = {
                        settingsManager.resetGlassTuning()
                        intensity = settingsManager.glassIntensity
                        transparency = settingsManager.glassTransparency
                        border = settingsManager.glassBorderOpacity
                        shadow = settingsManager.glassShadowIntensity
                        glow = settingsManager.glassGlowIntensity
                        ambient = settingsManager.glassAmbientEnabled
                        animations = settingsManager.glassAnimationsEnabled
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Live sample of the current settings. Reads the real `liquidGlass` modifier
 * through the real tuning provider, so what the user sees here is exactly what
 * the mini player and cards are doing.
 */
@Composable
private fun GlassPreviewCard(
    tuning: GlassUserTuning,
    glow: Float,
    isPlaying: Boolean
) {
    androidx.compose.runtime.CompositionLocalProvider(
        com.octadevs.resomusic.ui.theme.LocalGlassUserTuning provides tuning
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            com.octadevs.resomusic.ui.components.AmbientHalo(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(148.dp),
                strength = glow
            )
            GlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp),
                shape = RoundedCornerShape(28.dp),
                cornerRadius = 28.dp,
                strong = true,
                raised = true
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        stringResource(R.string.glass_preview_track),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlassSurface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            cornerRadius = 19.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        // Fake progress bar, purely to show how a track reads
                        // over the current glass strength.
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .weight(1f)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.38f)
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.tertiary
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassKnobGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 6.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title.uppercase(), style = MicroLabel, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(
                        Brush.horizontalGradient(
                            listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.28f), Color.Transparent)
                        )
                    )
            )
        }
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            cornerRadius = 24.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun GlassSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatPercent(value, range),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Slider(
            value = value.coerceIn(range.start, range.endInclusive),
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f)
            )
        )
    }
}

@Composable
private fun GlassToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        BouncySwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            thumbContent = {
                Icon(
                    imageVector = if (checked) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize)
                )
            }
        )
    }
}

@Composable
private fun GlassResetButton(onReset: () -> Unit) {
    androidx.compose.material3.TextButton(
        onClick = onReset,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .bounceClick()
    ) {
        Icon(
            Icons.Default.RestartAlt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(19.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            stringResource(R.string.reset_to_default),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Positions each value inside its own range, so "50%" always means the middle
 * of what that slider can do — not a number that means different things on
 * different rows.
 */
private fun formatPercent(value: Float, range: ClosedFloatingPointRange<Float>): String {
    val span = (range.endInclusive - range.start).takeIf { it > 0f } ?: return "0%"
    val t = ((value - range.start) / span).coerceIn(0f, 1f)
    return "${(t * 100).toInt()}%"
}
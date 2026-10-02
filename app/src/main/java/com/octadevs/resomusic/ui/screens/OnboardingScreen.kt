package com.octadevs.resomusic.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import com.octadevs.resomusic.ui.components.BouncySwitch
import com.octadevs.resomusic.ui.components.LiquidGlassBackground
import com.octadevs.resomusic.ui.components.liquidGlass
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.octadevs.resomusic.R
import com.octadevs.resomusic.tools.SettingsManager
import kotlinx.coroutines.delay
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    
    Crossfade(targetState = currentStep, label = "OnboardingCrossfade") { step ->
        when (step) {
            0 -> WelcomeStep(onStartClick = { currentStep = 1 })
            1 -> PermissionStep(onNext = { currentStep = 2 })
            2 -> BluetoothPermissionStep(onNext = { currentStep = 3 })
            3 -> NotificationPermissionStep(onNext = { currentStep = 4 })
            4 -> MusicPermissionStep(onNext = { currentStep = 5 })
            5 -> ManageFilesPermissionStep(onNext = { currentStep = 6 })
            6 -> FolderVisibilityStep(onNext = { currentStep = 7 })
            7 -> PermissionsReminderStep(onNext = { currentStep = 8 })
            8 -> SupportStep(onNext = { currentStep = 9 })
            9 -> FeaturesStep(onFinish = onStartClick)
        }
    }
}

/**
 * Aurora + grain backdrop shared by every onboarding step so the whole flow
 * feels like one continuous surface instead of a stack of flat screens.
 */
@Composable
private fun OnboardingBackdrop(isDarkTheme: Boolean, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        LiquidGlassBackground(
            isDarkTheme = isDarkTheme,
            meshStrength = if (isDarkTheme) 0.9f else 0.7f,
            baseColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.matchParentSize()
        )
        Box(modifier = Modifier.matchParentSize(), content = content)
    }
}

/**
 * The frosted pane that holds a step's copy, so text sits on glass rather than
 * directly on the background wash.
 */
@Composable
private fun OnboardingGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(30.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (isDarkTheme) 0.13f else 0.58f),
                        Color.White.copy(alpha = if (isDarkTheme) 0.06f else 0.34f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0.08f)
                    )
                ),
                shape = shape
            )
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

/**
 * Glass pill CTA. Replaces the flat filled buttons so every action in the flow
 * shares the same material.
 */
@Composable
private fun OnboardingGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    strong: Boolean = true,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(30.dp)
    val alpha = if (enabled) 1f else 0.45f
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .height(58.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = (if (isDarkTheme) 0.22f else 0.92f) * alpha),
                        Color.White.copy(alpha = (if (isDarkTheme) 0.12f else 0.62f) * alpha)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.7f * alpha),
                        Color.White.copy(alpha = 0.14f * alpha)
                    )
                ),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 30.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

/**
 * Soft glow disc that sits behind the rotating logo mark.
 */
@Composable
private fun LogoAura(isDarkTheme: Boolean, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "AuraPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraPulseValue"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = pulse }
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isDarkTheme) 0.16f else 0.62f),
                        Color.Transparent
                    )
                )
            )
    )
}

/**
 * The emblem at the top of each permission step.
 *
 * This used to be the 1024x1024 logo bitmap with the step's own icon dropped
 * on top of it. That read badly, and for a specific reason: the bitmap is 87%
 * pure black, so what actually rendered was a large dark square with a small
 * icon marooned in the middle of it, while the mark inside the square was far
 * larger than the icon it was meant to be illustrating. Every permission page
 * ended up showing the same black square, which is what made the flow look
 * broken rather than branded.
 *
 * One glass disc holding the step's icon says the same thing, reads
 * instantly, and is built from the app's own glass so it still belongs. It
 * also removes a 1024x1024 decode from the very first screen the user sees.
 */
@Composable
private fun OnboardingStepEmblem(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 112.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .liquidGlass(
                    shape = CircleShape,
                    cornerRadius = size / 2,
                    strong = true,
                    raised = true
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(size * 0.42f)
            )
        }
    }
}
@Composable
fun WelcomeStep(onStartClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val diamondsColor = if (isDark) Color.White else Color.Black
    val noteColor = if (isDark) Color.Black else Color.White


    OnboardingBackdrop(isDarkTheme = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(176.dp)
            ) {
                LogoAura(isDarkTheme = isDark, modifier = Modifier.size(196.dp))
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.new_reso_logo),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier.size(132.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp,
                letterSpacing = (-1).sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(28.dp))

            OnboardingGlassCard {
                Text(
                    text = stringResource(id = R.string.onboarding_description),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            OnboardingGlassButton(onClick = onStartClick, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.onboarding_start_button),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun PermissionStep(onNext: () -> Unit) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val diamondsColor = if (isDark) Color.White else Color.Black
    val iconColor = if (isDark) Color.Black else Color.White

    var isPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        isPermissionGranted = granted
    }


    OnboardingBackdrop(isDarkTheme = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingStepEmblem(
                imageVector = Icons.Default.Mic
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${stringResource(R.string.onboarding_perm_audio_title)} - ${stringResource(R.string.onboarding_perm_required)}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.onboarding_perm_audio_desc),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            OnboardingGlassButton(
                onClick = {
                    if (isPermissionGranted) {
                        onNext()
                    } else {
                        launcher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isPermissionGranted) stringResource(R.string.onboarding_next_button) else stringResource(R.string.onboarding_grant_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isPermissionGranted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (!isPermissionGranted) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onNext) {
                    Text(
                        text = stringResource(R.string.onboarding_skip_button),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun BluetoothPermissionStep(onNext: () -> Unit) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val diamondsColor = if (isDark) Color.White else Color.Black
    val iconColor = if (isDark) Color.Black else Color.White

    val bluetoothPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Manifest.permission.BLUETOOTH_CONNECT
    } else {
        null
    }

    var isPermissionGranted by remember {
        mutableStateOf(
            bluetoothPermission == null || ContextCompat.checkSelfPermission(context, bluetoothPermission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        isPermissionGranted = granted
    }


    OnboardingBackdrop(isDarkTheme = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingStepEmblem(
                imageVector = Icons.Default.Bluetooth
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${stringResource(R.string.onboarding_perm_bluetooth_title)} - ${stringResource(R.string.onboarding_perm_required)}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.onboarding_perm_bluetooth_desc),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            OnboardingGlassButton(
                onClick = {
                    if (isPermissionGranted) {
                        onNext()
                    } else {
                        bluetoothPermission?.let { launcher.launch(it) }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isPermissionGranted) stringResource(R.string.onboarding_next_button) else stringResource(R.string.onboarding_grant_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isPermissionGranted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (!isPermissionGranted) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onNext) {
                    Text(
                        text = stringResource(R.string.onboarding_skip_button),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationPermissionStep(onNext: () -> Unit) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val diamondsColor = if (isDark) Color.White else Color.Black
    val iconColor = if (isDark) Color.Black else Color.White

    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else {
        null
    }

    var isPermissionGranted by remember {
        mutableStateOf(
            notificationPermission == null || ContextCompat.checkSelfPermission(context, notificationPermission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        isPermissionGranted = granted
    }


    OnboardingBackdrop(isDarkTheme = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingStepEmblem(
                imageVector = Icons.Default.Notifications
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${stringResource(R.string.onboarding_perm_notifications_title)} - ${stringResource(R.string.onboarding_perm_required)}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.onboarding_perm_notifications_desc),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            OnboardingGlassButton(
                onClick = {
                    if (isPermissionGranted) {
                        onNext()
                    } else {
                        notificationPermission?.let { launcher.launch(it) }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isPermissionGranted) stringResource(R.string.onboarding_next_button) else stringResource(R.string.onboarding_grant_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isPermissionGranted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MusicPermissionStep(onNext: () -> Unit) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val diamondsColor = if (isDark) Color.White else Color.Black
    val iconColor = if (isDark) Color.Black else Color.White

    val musicPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var isPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, musicPermission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        isPermissionGranted = granted
    }


    OnboardingBackdrop(isDarkTheme = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingStepEmblem(
                imageVector = Icons.Default.MusicNote
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${stringResource(R.string.onboarding_perm_music_title)} - ${stringResource(R.string.onboarding_perm_required)}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.onboarding_perm_music_desc),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            OnboardingGlassButton(
                onClick = {
                    if (isPermissionGranted) {
                        onNext()
                    } else {
                        launcher.launch(musicPermission)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isPermissionGranted) stringResource(R.string.onboarding_next_button) else stringResource(R.string.onboarding_grant_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isPermissionGranted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ManageFilesPermissionStep(onNext: () -> Unit) {
    val context = LocalContext.current
    val settingsManager = SettingsManager.getInstance(context)
    val isDark = isSystemInDarkTheme()
    val diamondsColor = if (isDark) Color.White else Color.Black
    val iconColor = if (isDark) Color.Black else Color.White

    var isPermissionGranted by remember {
        mutableStateOf(settingsManager.musicFolderUri != null)
    }

    val initialSafUri = remember {
        try {
            val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            if (musicDir.exists()) {
                DocumentsContract.buildTreeDocumentUri(
                    "com.android.externalstorage.documents",
                    "primary:${musicDir.name}"
                )
            } else null
        } catch (_: Exception) { null }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                settingsManager.musicFolderUri = uri.toString()
                settingsManager.isInitialFolderScanPending = true
                isPermissionGranted = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    OnboardingBackdrop(isDarkTheme = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingStepEmblem(
                imageVector = Icons.Default.Folder
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${stringResource(R.string.onboarding_perm_manage_files_title)}${if (isPermissionGranted) "" else " - " + stringResource(R.string.onboarding_perm_required)}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.onboarding_perm_manage_files_desc),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            OnboardingGlassButton(
                onClick = {
                    if (isPermissionGranted) {
                        onNext()
                    } else {
                        launcher.launch(initialSafUri)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isPermissionGranted) stringResource(R.string.onboarding_next_button) else stringResource(R.string.onboarding_grant_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isPermissionGranted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FolderVisibilityStep(onNext: () -> Unit) {
    val context = LocalContext.current
    val settingsManager = SettingsManager.getInstance(context)
    val isDark = isSystemInDarkTheme()
    val diamondsColor = if (isDark) Color.White else Color.Black
    val iconColor = if (isDark) Color.Black else Color.White

    var showAll by remember {
        mutableStateOf(settingsManager.showAllFoldersOnStart)
    }


    OnboardingBackdrop(isDarkTheme = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingStepEmblem(
                imageVector = Icons.Default.Visibility
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.onboarding_folder_visibility_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.onboarding_folder_visibility_desc),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                onClick = { showAll = !showAll },
                shape = RoundedCornerShape(22.dp),
                color = if (showAll)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.32f)
                else
                    Color.White.copy(alpha = if (isDark) 0.08f else 0.45f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.42f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_folder_visibility_label),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    BouncySwitch(
                        checked = showAll,
                        onCheckedChange = { showAll = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            OnboardingGlassButton(
                onClick = {
                    settingsManager.showAllFoldersOnStart = showAll
                    onNext()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.onboarding_next_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun FeaturesStep(onFinish: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val diamondsColor = if (isDark) Color.White else Color.Black
    val noteColor = if (isDark) Color.Black else Color.White
    val colorPrimary = MaterialTheme.colorScheme.primary

    var isExploding by remember { mutableStateOf(false) }
    
    val explosionProgress = remember { androidx.compose.animation.core.Animatable(0f) }
    val fadeProgress = remember { androidx.compose.animation.core.Animatable(0f) }

    val features = listOf(
        FeatureItem(stringResource(R.string.onboarding_feature_hifi), Icons.Default.GraphicEq),

        FeatureItem(stringResource(R.string.onboarding_feature_title), Icons.Default.Title),
        FeatureItem(stringResource(R.string.onboarding_feature_lyrics), Icons.Default.Lyrics),
        FeatureItem(stringResource(R.string.onboarding_feature_mix), Icons.Default.AutoAwesome),
        FeatureItem(stringResource(R.string.onboarding_feature_more), Icons.Default.Add)
    )

    val pagerState = rememberPagerState(pageCount = { features.size })

    // Particle state
    val particles = remember {
        List(40) {
            val angle = Math.random() * 2 * Math.PI
            val speed = 120f + (Math.random() * 480f).toFloat()
            val vx = (Math.cos(angle) * speed).toFloat()
            val vy = (Math.sin(angle) * speed).toFloat()
            Particle(vx, vy)
        }
    }

    LaunchedEffect(isExploding) {
        if (isExploding) {
            explosionProgress.animateTo(
                1f,
                animationSpec = tween(durationMillis = 850, easing = LinearOutSlowInEasing)
            )
            delay(100)
            fadeProgress.animateTo(
                1f,
                animationSpec = tween(durationMillis = 650, easing = LinearEasing)
            )
            onFinish()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = 1f - fadeProgress.value
            }
    ) {
        OnboardingBackdrop(isDarkTheme = isDark) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(150.dp)
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.new_reso_logo),
                            contentScale = ContentScale.Fit,
                            contentDescription = null,
                            modifier = Modifier.size(104.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.onboarding_welcome_back),
                        style = MaterialTheme.typography.headlineMedium,
                        color = colorPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.onboarding_listen_style),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Carousel Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentPadding = PaddingValues(horizontal = 64.dp),
                        pageSpacing = 16.dp
                    ) { page ->
                        val item = features[page]
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                ).coerceIn(-1f, 1f)
                        
                        val scale = 1f - (Math.abs(pageOffset) * 0.15f)
                        val alpha = 1f - (Math.abs(pageOffset) * 0.7f)

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    this.alpha = alpha
                                }
                                .clip(RoundedCornerShape(28.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = if (isDark) 0.13f else 0.55f),
                                            Color.White.copy(alpha = if (isDark) 0.06f else 0.28f)
                                        )
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.5f),
                                            Color.White.copy(alpha = 0.08f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = colorPrimary.copy(alpha = 0.1f),
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = colorPrimary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Pager Indicators
                    Row(
                        modifier = Modifier.height(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(features.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) colorPrimary else MaterialTheme.colorScheme.outlineVariant
                            val width by animateDpAsState(
                                targetValue = if (pagerState.currentPage == iteration) 24.dp else 8.dp,
                                label = "IndicatorWidth"
                            )
                            Box(
                                modifier = Modifier
                                    .size(width = width, height = 8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Footer
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    OnboardingGlassButton(
                        onClick = { isExploding = true },
                        modifier = Modifier
                            .width(220.dp)
                            .alpha(if (isExploding) 0f else 1f)
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_finish_button),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (isExploding) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.size(220.dp, 56.dp)) {
                            particles.forEach { particle ->
                                val progress = explosionProgress.value
                                val x = center.x + (particle.vx * progress)
                                val y = center.y + (particle.vy * progress)
                                val size = 18f * (1f - progress)
                                
                                drawCircle(
                                    color = colorPrimary,
                                    radius = size,
                                    center = androidx.compose.ui.geometry.Offset(x, y),
                                    alpha = 1f - progress
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Particle(val vx: Float, val vy: Float)
data class FeatureItem(val title: String, val icon: ImageVector)

@Composable
fun PermissionsReminderStep(onNext: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val diamondsColor = MaterialTheme.colorScheme.primary
    val iconColor = MaterialTheme.colorScheme.onSurface


    OnboardingBackdrop(isDarkTheme = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingStepEmblem(
                imageVector = androidx.compose.material.icons.Icons.Default.Security
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.onboarding_reminder_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.onboarding_reminder_desc),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            OnboardingGlassButton(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.onboarding_reminder_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun SupportStep(onNext: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val diamondsColor = if (isDark) Color.White else Color.Black
    val iconColor = if (isDark) Color.Black else Color.White


    OnboardingBackdrop(isDarkTheme = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingStepEmblem(
                imageVector = Icons.Default.Favorite
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.onboarding_support_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.onboarding_support_desc),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            OnboardingGlassButton(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.onboarding_next_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


package com.octadevs.resomusic.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun <T> FastScrollbar(
    listState: LazyListState,
    items: List<T>,
    modifier: Modifier = Modifier,
    headerItemCount: Int = 0,
    itemKeyOrLetter: (T) -> String,
    thumbColor: Color = MaterialTheme.colorScheme.primary,
    bubbleColor: Color = MaterialTheme.colorScheme.primaryContainer,
    bubbleTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    if (items.size <= 15) return

    val totalItems = listState.layoutInfo.totalItemsCount
    val songItemsCount = (totalItems - headerItemCount).coerceAtLeast(0)
    if (songItemsCount <= 1) return

    val coroutineScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var containerHeight by remember { mutableFloatStateOf(0f) }
    var currentLetter by remember { mutableStateOf("") }
    var frozenTopPx by remember { mutableFloatStateOf(0f) }

    val thumbHeightDp = 48.dp
    val density = LocalDensity.current
    val thumbHeightPx = with(density) { thumbHeightDp.toPx() }

    val dynamicTopPx = remember(listState.layoutInfo, headerItemCount) {
        if (headerItemCount > 0) {
            val headerInfo = listState.layoutInfo.visibleItemsInfo.find { it.index == headerItemCount - 1 }
            if (headerInfo != null) {
                (headerInfo.offset + headerInfo.size).toFloat().coerceAtLeast(0f)
            } else 0f
        } else 0f
    }

    val topPx = if (isDragging) frozenTopPx else dynamicTopPx
    val topPaddingDp = with(density) { topPx.toDp() }

    val firstSongVisible = (listState.firstVisibleItemIndex - headerItemCount).coerceAtLeast(0)
    val progress = remember(firstSongVisible, songItemsCount) {
        if (songItemsCount > 1) {
            (firstSongVisible.toFloat() / (songItemsCount - 1)).coerceIn(0f, 1f)
        } else 0f
    }

    val maxTrack = (containerHeight - thumbHeightPx).coerceAtLeast(0f)
    val currentThumbY = if (isDragging) dragOffset.coerceIn(0f, maxTrack) else (progress * maxTrack)

    val thumbWidth by animateDpAsState(
        targetValue = if (isDragging) 10.dp else 4.dp,
        animationSpec = tween(durationMillis = 150),
        label = "thumbWidth"
    )
    val thumbAlpha by animateFloatAsState(
        targetValue = if (isDragging) 1f else 0.4f,
        animationSpec = tween(durationMillis = 150),
        label = "thumbAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(top = topPaddingDp)
            .width(24.dp)
            .onGloballyPositioned { containerHeight = it.size.height.toFloat() }
            .pointerInput(containerHeight, totalItems, items.size, headerItemCount) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    frozenTopPx = dynamicTopPx
                    isDragging = true

                    fun updatePosition(y: Float) {
                        val maxTrk = containerHeight - thumbHeightPx
                        val newY = (y - thumbHeightPx / 2f).coerceIn(0f, maxTrk.coerceAtLeast(0f))
                        dragOffset = newY
                        val targetProgress = if (maxTrk > 0f) (newY / maxTrk).coerceIn(0f, 1f) else 0f
                        
                        val targetSongIndex = (targetProgress * (songItemsCount - 1)).roundToInt().coerceIn(0, (songItemsCount - 1).coerceAtLeast(0))
                        
                        val targetIndex = if (targetProgress == 0f && headerItemCount > 0) 0 else headerItemCount + targetSongIndex
                        
                        val actualItemIndex = targetIndex - headerItemCount
                        val newLetter = if (actualItemIndex in items.indices) {
                            val str = itemKeyOrLetter(items[actualItemIndex]).trim()
                            if (str.isNotEmpty()) str.take(1).uppercase() else ""
                        } else ""

                        if (newLetter != currentLetter) {
                            currentLetter = newLetter
                            if (newLetter.isNotBlank()) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }

                        coroutineScope.launch {
                            listState.scrollToItem(targetIndex)
                        }
                    }

                    updatePosition(down.position.y)

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break
                        if (change.pressed) {
                            change.consume()
                            updatePosition(change.position.y)
                        } else {
                            isDragging = false
                            break
                        }
                    }
                }
            }
    ) {
        // Thumb
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(-with(density) { 4.dp.roundToPx() }, currentThumbY.roundToInt()) }
                .width(thumbWidth)
                .height(thumbHeightDp)
                .clip(RoundedCornerShape(percent = 50))
                .background(thumbColor.copy(alpha = thumbAlpha))
        )

        // Bubble Indicator (Vertical Pill)
        AnimatedVisibility(
            visible = isDragging && currentLetter.isNotEmpty(),
            enter = fadeIn() + scaleIn(transformOrigin = TransformOrigin(1f, 0.5f)),
            exit = fadeOut() + scaleOut(transformOrigin = TransformOrigin(1f, 0.5f)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { 
                    IntOffset(
                        -with(density) { 56.dp.roundToPx() }, 
                        (currentThumbY - with(density) { 12.dp.toPx() }).roundToInt()
                    ) 
                }
        ) {
            Surface(
                shape = RoundedCornerShape(percent = 50),
                color = bubbleColor,
                shadowElevation = 8.dp,
                modifier = Modifier.width(48.dp).height(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = currentLetter,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = bubbleTextColor
                    )
                }
            }
        }
    }
}

package com.octadevs.resomusic.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.octadevs.resomusic.R
import com.octadevs.resomusic.tools.Song
import com.octadevs.resomusic.ui.components.SongCoverImage
import com.octadevs.resomusic.ui.theme.MicroLabel
import com.octadevs.resomusic.ui.utils.bounceClick

@Composable
fun RecommendationSection(
    title: String,
    songs: List<Song>,
    hasBlurBackground: Boolean = false,
    onSongClick: (Song) -> Unit,
) {
    if (songs.isEmpty()) return

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (hasBlurBackground) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(songs, key = { it.id }) { song ->
                RecommendationCard(
                    song = song,
                    onClick = { onSongClick(song) }
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    song: Song,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    // Media scanners hand back literal "<unknown>" tags; showing that is noise.
    val displayArtist = song.artist.trim()
        .takeIf { it.isNotEmpty() && !it.equals("<unknown>", ignoreCase = true) && !it.equals("unknown", ignoreCase = true) }
        ?.let { context.getString(R.string.unknown_artist) }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        modifier = Modifier
            .width(158.dp)
            .height(198.dp)
            .bounceClick()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SongCoverImage(
                coverUrl = song.coverUrl ?: song.albumArtUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(0.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.2f),
                                Color.Black.copy(alpha = 0.75f),
                                Color.Black.copy(alpha = 0.92f)
                            ),
                            startY = 0f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = displayArtist ?: context.getString(R.string.unknown_artist),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White.copy(alpha = 0.78f)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    hasBlurBackground: Boolean = false
) {
    val accent = MaterialTheme.colorScheme.primary
    val onGlass = if (hasBlurBackground) Color.White else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 6.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MicroLabel,
            color = if (hasBlurBackground) Color.White.copy(alpha = 0.62f) else accent
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                brush = Brush.linearGradient(
                    listOf(onGlass, onGlass.copy(alpha = 0.68f))
                )
            ),
            maxLines = 1
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.horizontalGradient(listOf(accent, Color.Transparent))
                )
        )
    }
}

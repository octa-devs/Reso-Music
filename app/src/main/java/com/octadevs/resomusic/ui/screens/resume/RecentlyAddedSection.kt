package com.octadevs.resomusic.ui.screens.resume

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.octadevs.resomusic.R
import com.octadevs.resomusic.tools.Song
import com.octadevs.resomusic.ui.components.glassPane
import com.octadevs.resomusic.ui.components.SongCoverImage
import com.octadevs.resomusic.ui.utils.formatDuration

@Composable
fun RecentlyAddedSection(
    songs: List<Song>,
    hasBlurBackground: Boolean = false,
    onSongClick: (Song) -> Unit,
) {
    if (songs.isEmpty()) return

    Column {
        SectionHeader(
            title = stringResource(R.string.resume_recently_added),
            hasBlurBackground = hasBlurBackground
        )
        // Plain Column, not a LazyColumn.
        //
        // This sits inside ResumeScreen's verticalScroll Column. A nested
        // vertical list inside a vertical scroll steals the parent's gestures,
        // so the Home page would appear to "stick" for ~320dp before the page
        // scrolled at all. It only ever holds five rows, so it does not need
        // to virtualise anything.
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            songs.take(5).forEach { song ->
                RecentlyAddedRow(
                    song = song,
                    hasBlurBackground = hasBlurBackground,
                    onClick = { onSongClick(song) }
                )
            }
        }
    }
}

@Composable
private fun RecentlyAddedRow(
    song: Song,
    hasBlurBackground: Boolean = false,
    onClick: () -> Unit,
) {
    val titleColor = if (hasBlurBackground) Color.White else MaterialTheme.colorScheme.onSurface
    val artistColor = if (hasBlurBackground) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
    val durationColor = if (hasBlurBackground) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
    val rowBg = if (hasBlurBackground) Color.Black.copy(alpha = 0.22f) else glassPane(0.3f)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = rowBg,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            SongCoverImage(
                coverUrl = song.coverUrl ?: song.albumArtUri,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = titleColor
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = artistColor
                )
            }
            Text(
                text = formatDuration(song.duration),
                style = MaterialTheme.typography.labelSmall,
                color = durationColor
            )
        }
    }
}



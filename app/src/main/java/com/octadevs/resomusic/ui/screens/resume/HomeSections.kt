package com.octadevs.resomusic.ui.screens.resume

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.octadevs.resomusic.R
import com.octadevs.resomusic.tools.Song
import com.octadevs.resomusic.ui.components.SongCoverImage
import com.octadevs.resomusic.ui.components.liquidGlass
import com.octadevs.resomusic.ui.theme.CardSubtitle
import com.octadevs.resomusic.ui.theme.CardTitle
import com.octadevs.resomusic.ui.theme.EmberOrange
import com.octadevs.resomusic.ui.theme.EmberAmber
import com.octadevs.resomusic.ui.theme.PillLabel
import com.octadevs.resomusic.ui.theme.Radius
import com.octadevs.resomusic.ui.theme.Space

/* ============================================================
   HOME — category pills + immersive carousel
   ------------------------------------------------------------
   Proportions here are taken from the reference layout rather
   than chosen by eye:

     · ~5% of screen width inset on both sides (20px of 400px)
     · carousel card ~69% of the available width, which leaves the
       next card visibly peeking — the peek is what signals
       "this scrolls horizontally"
     · card aspect ~1.28:1 (250x195 measured), artwork filling the
       card with the title/artist carried on a bottom scrim
   ============================================================ */

/** A top-level destination. Backed by the real tab list, never invented. */
data class HomeTab(
    val id: String,
    val label: String
)

private val PillShape = RoundedCornerShape(Radius.pill)

/**
 * Horizontally scrollable category filters.
 *
 * These drive the app's real sections — the ids are the same tab ids the
 * pager and the section sheet already use, so tapping a pill is genuine
 * navigation rather than a decorative control.
 *
 * Selected state is translucent coloured glass with a check, matching the
 * reference, rather than a filled Material button.
 */
@Composable
fun CategoryPills(
    tabs: List<HomeTab>,
    activeTabId: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tabs.isEmpty()) return
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = Space.lg),
        horizontalArrangement = Arrangement.spacedBy(Space.sm)
    ) {
        items(tabs, key = { it.id }) { tab ->
            CategoryPill(
                label = tab.label,
                selected = tab.id == activeTabId,
                onClick = { onTabSelected(tab.id) }
            )
        }
    }
}

@Composable
private fun CategoryPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    // The selected pill is purple *glass*; unselected is a neutral frost.
    val tint = if (selected) EmberOrange else Color.White
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = spring(),
        label = "pillContent"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(42.dp)
            .clip(PillShape)
            // `liquidGlass` supplies the fill, rim, specular sheen and shadow;
            // the tint parameter is what separates selected from unselected.
            .liquidGlass(
                shape = PillShape,
                cornerRadius = Radius.pill,
                strong = selected,
                raised = selected,
                tint = tint
            )
            .categoryPillInteraction(
                onClick = onClick,
                label = label,
                isSelected = selected
            )
            .padding(horizontal = if (selected) Space.md else Space.lg)
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = if (isDark) EmberAmber else EmberOrange,
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = label,
            style = PillLabel,
            color = contentColor,
            maxLines = 1
        )
    }
}

/**
 * Click behaviour + a11y semantics in one modifier.
 *
 * Kept separate so the pill's glass modifiers compose independently of its
 * interaction behaviour. Marked `@Composable` because `Modifier.clickable`
 * reads `LocalIndication`.
 */
@Composable
private fun Modifier.categoryPillInteraction(
    onClick: () -> Unit,
    label: String,
    isSelected: Boolean
): Modifier = this
    .semantics {
        role = Role.Tab
        selected = isSelected
        contentDescription = label
    }
    .clickable(onClick = onClick)

/**
 * "Recently Played" as a large horizontal carousel.
 *
 * Shows real listening history from `playback_stats.lastPlayed`; renders
 * nothing at all when there is no history yet rather than substituting
 * invented content.
 */
@Composable
fun RecentlyPlayedCarousel(
    title: String,
    songs: List<Song>,
    onSongClick: (Song) -> Unit
) {
    if (songs.isEmpty()) return
    val context = LocalContext.current
    val cardShape = RoundedCornerShape(22.dp)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Shares the one SectionHeader with every other Home section, so the
        // whole page has a single heading rhythm.
        SectionHeader(title = title)

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val available = maxWidth - Space.lg * 2
            // 69% leaves a clear peek of the next card.
            val cardWidth = (available * 0.69f).coerceIn(180.dp, 300.dp)

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Space.md),
                contentPadding = PaddingValues(horizontal = Space.lg)
            ) {
                items(songs, key = { it.id }) { song ->
                    ImmersiveSongCard(
                        song = song,
                        modifier = Modifier
                            .width(cardWidth)
                            .aspectRatio(1.28f),
                        shape = cardShape,
                        unknownArtist = context.getString(R.string.unknown_artist),
                        onClick = { onSongClick(song) }
                    )
                }
            }
        }
    }
}

/**
 * The large artwork card from the reference.
 *
 * Artwork fills the card edge to edge with a bottom scrim carrying the
 * metadata — no border, and only a whisper of glass, so the artwork stays
 * the loudest thing on screen.
 */
@Composable
private fun ImmersiveSongCard(
    song: Song,
    modifier: Modifier,
    shape: RoundedCornerShape,
    unknownArtist: String,
    onClick: () -> Unit
) {
    val artist = song.artist.trim().takeIf {
        it.isNotEmpty() && !it.equals("<unknown>", ignoreCase = true)
    } ?: unknownArtist

    Surface(
        onClick = onClick,
        shape = shape,
        color = Color.Transparent,
        modifier = modifier.semantics {
            contentDescription = "${song.title}, $artist"
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SongCoverImage(
                coverUrl = song.coverUrl ?: song.albumArtUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(0.dp),
                contentScale = ContentScale.Crop,
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )

            // Scrim. Strong at the very bottom where the text sits, clear at
            // the top so the artwork is not dulled.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.10f),
                            0.42f to Color.Transparent,
                            0.70f to Color.Black.copy(alpha = 0.55f),
                            1f to Color.Black.copy(alpha = 0.90f)
                        )
                    )
            )

            // A faint purple rim, matching the rest of the material language.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.White.copy(alpha = 0.10f), shape)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = song.title,
                    style = CardTitle,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = artist,
                    style = CardSubtitle,
                    color = Color.White.copy(alpha = 0.76f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * "Most Played" — shown only when there is genuinely tracked play-count data.
 *
 * Returns an empty list rather than a fabricated ranking if nothing has been
 * counted yet.
 */
fun resolveMostPlayed(
    stats: List<com.octadevs.resomusic.data.PlaybackStats>,
    allSongs: List<Song>
): List<Song> = stats
    // Only songs whose play count is actually incremented are eligible. The
    // time-only writes also create rows, and ranking those would be fiction.
    .filter { it.playCount > 0 }
    .sortedByDescending { it.playCount }
    .mapNotNull { stat ->
        stat.id.removePrefix("SONG_").toLongOrNull()
            ?.let { id -> allSongs.find { it.id == id } }
    }
    .distinctBy { it.id }

/**
 * "Recently Played" — the real persisted listening history.
 */
fun resolveRecentlyPlayed(
    stats: List<com.octadevs.resomusic.data.PlaybackStats>,
    allSongs: List<Song>
): List<Song> = stats
    .filter { it.lastPlayed > 0 }
    .sortedByDescending { it.lastPlayed }
    .mapNotNull { stat ->
        stat.id.removePrefix("SONG_").toLongOrNull()
            ?.let { id -> allSongs.find { it.id == id } }
    }
    .distinctBy { it.id }
package com.octadevs.resomusic.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.octadevs.resomusic.R
import com.octadevs.resomusic.ui.screens.resume.HomeTab
import com.octadevs.resomusic.ui.theme.EmberOrange
import com.octadevs.resomusic.ui.theme.quicksand

/* ============================================================
   FLOATING NAVIGATION DOCK
   ------------------------------------------------------------
   The reference's bottom navigation is not a Material bottom bar:
   it floats clear of the system inset, is wide and heavily rounded,
   is frosted glass rather than a filled surface, and marks the active
   destination with a *subtle* highlight rather than a coloured block.

   Measured from the reference: a ~10%-of-screen-height assembly
   sitting ~5% in from each side, and notably *brighter* than the album
   artwork behind it -- so the glass here is deliberately the most
   luminous material in the app.
   ============================================================ */

private val DockShape = RoundedCornerShape(28.dp)
private val DockItemShape = RoundedCornerShape(13.dp)

/** Tab ids are plain strings throughout the app; this maps them to icons. */
fun navIconForTab(id: String): ImageVector = when (id) {
    "RESUME" -> Icons.Filled.Spa
    "MIXES" -> Icons.Filled.AutoAwesome
    "ALL" -> Icons.Filled.QueueMusic
    "PLAYLISTS" -> Icons.Filled.Album
    "FAVORITES" -> Icons.Filled.Favorite
    "ALBUMS" -> Icons.Filled.Album
    "ARTISTS" -> Icons.Filled.Person
    "GENRES" -> Icons.Filled.GridView
    "FOLDERS" -> Icons.Filled.Folder
    else -> Icons.Filled.QueueMusic
}

/**
 * Floating glass navigation bar.
 *
 * Shows at most [maxVisible] destinations and then a "More" affordance that
 * hands off to the existing full section sheet, so nothing becomes
 * unreachable on a small screen no matter how many tabs exist.
 */
@Composable
fun FloatingNavDock(
    tabs: List<HomeTab>,
    activeTabId: String,
    onTabSelected: (String) -> Unit,
    onMoreClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    maxVisible: Int = 4
) {
    if (tabs.isEmpty()) return

    // Everything past the limit collapses into "More" -> the section sheet.
    val visible = tabs.take(maxVisible)
    val overflow = tabs.drop(maxVisible)
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(DockShape)
            // The dock is the app's most prominent glass surface, hence
            // strong + raised: full rim, specular sheen and a real shadow.
            .liquidGlass(
                shape = DockShape,
                cornerRadius = 28.dp,
                strong = true,
                raised = true
            )
            .padding(horizontal = 6.dp, vertical = 9.dp),
        // Top-aligned, not centre-aligned: every cell is the same height, so
        // centring only lets a taller cell push its own label out of line with
        // its neighbours. Top alignment puts all the icons on one row and all
        // the labels on the next, which is what makes the bar read as a bar.
        verticalAlignment = Alignment.Top
    ) {
        visible.forEach { tab ->
            DockItem(
                label = tab.label,
                icon = navIconForTab(tab.id),
                isActive = tab.id == activeTabId,
                isDark = isDark,
                onClick = { onTabSelected(tab.id) },
                modifier = Modifier.weight(1f)
            )
        }

        if (overflow.isNotEmpty()) {
            DockItem(
                label = stringResource(R.string.nav_more),
                icon = Icons.Filled.MoreHoriz,
                isActive = overflow.any { it.id == activeTabId },
                isDark = isDark,
                onClick = onMoreClick,
                modifier = Modifier.weight(1f)
            )
        }

        DockItem(
            label = stringResource(R.string.nav_settings),
            icon = Icons.Filled.Settings,
            isActive = false,
            isDark = isDark,
            onClick = onSettingsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * A single dock destination.
 *
 * The active state is a soft purple glass highlight sized to the icon rather
 * than filling the cell — the brief is explicit about not wanting "a giant
 * colored box".
 */
@Composable
private fun DockItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor by animateColorAsState(
        targetValue = when {
            isActive && isDark -> Color.White
            isActive -> EmberOrange
            isDark -> Color.White.copy(alpha = 0.55f)
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spring(),
        label = "dockIconTint"
    )

    val highlight by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "dockHighlight"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .semantics {
                role = Role.Tab
                selected = isActive
                // The Text below already announces itself; without this the
                // label is read out twice.
                contentDescription = ""
            }
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(DockItemShape)
                .background(
                    EmberOrange.copy(
                        alpha = (if (isDark) 0.55f else 0.18f) * highlight
                    )
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            // Deliberately not MicroLabel.copy().
            //
            // MicroLabel is a 2.4sp-tracked all-caps eyebrow. Tracking adds a
            // trailing gap after the final character, so a centred label sits
            // half a step left of true centre -- by a different amount for
            // every label, which is exactly the ragged column this is fixing.
            // Zero tracking plus a full-width text box means every label
            // occupies the same rectangle and shares one baseline.
            style = TextStyle(
                fontFamily = quicksand,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp
            ),
            color = contentColor,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
        )
    }
}
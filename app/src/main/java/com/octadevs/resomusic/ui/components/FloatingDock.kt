package com.octadevs.resomusic.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
// Needed for the `by` delegation on animateColorAsState / animateFloatAsState.
// The compiler resolves `by` through this operator, not by name, so an unused-
// import sweep will always look like it is safe to drop.
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
import androidx.compose.ui.unit.dp
import com.octadevs.resomusic.R
import com.octadevs.resomusic.ui.screens.resume.HomeTab
import com.octadevs.resomusic.ui.theme.EmberOrange

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
            .height(58.dp)
            .clip(DockShape)
            // The dock is the app's most prominent glass surface, hence
            // strong + raised: full rim, specular sheen and a real shadow.
            .liquidGlass(
                shape = DockShape,
                cornerRadius = 29.dp,
                strong = true,
                raised = true
            )
            // 58 - 2x5 = 48dp per cell, which is the minimum touch target.
            .padding(horizontal = 6.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
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
 * A single dock destination: an icon, nothing else.
 *
 * The labels are gone. At 9-10sp under a 1-1.5dp glass rim they were never
 * legible on the glass, they forced the bar taller than the icons needed, and
 * they were the source of every alignment complaint about this component --
 * label lengths differ, so any text under the icons either had to be
 * truncated to match or read as a ragged row. The icons alone are unambiguous
 * and the bar reads as one object.
 *
 * Removing them is not an accessibility regression: [label] is now carried by
 * the node's contentDescription instead of by a visible Text, so TalkBack
 * announces exactly the same word it did before.
 *
 * The active state is a soft ember glass highlight sized to the icon rather
 * than filling the cell -- the brief is explicit about not wanting "a giant
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

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(DockItemShape)
            .background(
                EmberOrange.copy(
                    alpha = (if (isDark) 0.55f else 0.18f) * highlight
                )
            )
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Tab
                selected = isActive
                contentDescription = label
            }
    ) {
        Icon(
            imageVector = icon,
            // The parent node announces the label; leaving this null keeps
            // TalkBack from reading the destination name twice.
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(23.dp)
        )
    }
}
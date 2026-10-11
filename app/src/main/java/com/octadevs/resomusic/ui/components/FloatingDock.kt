package com.octadevs.resomusic.ui.components

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
// Needed for the `by` delegation on animateColorAsState / mutableStateOf. The
// compiler resolves `by` through this operator, not by name, so an unused-
// import sweep will always look like it is safe to drop.
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.octadevs.resomusic.R
import com.octadevs.resomusic.ui.screens.resume.HomeTab
import com.octadevs.resomusic.ui.theme.EmberOrange
import dev.chrisbanes.haze.HazeInput
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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

   The active highlight is a Liquid Glass capsule that springs between
   destinations (see [selectionBubble]). It replaced an alpha-faded
   ember wash painted into each cell: an element *inside* the glass
   reads as a thing sitting on the bar, where a tinted background reads
   as the bar itself changing colour.
   ============================================================ */

private val DockShape = RoundedCornerShape(28.dp)
private val DockItemShape = RoundedCornerShape(13.dp)

/**
 * Measurement key for the conditional "More" cell.
 *
 * It cannot collide with a real tab id, which are uppercase section names.
 */
private const val OVERFLOW_KEY = "__dock_more"

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
    maxVisible: Int = 4,
    /**
     * Backdrop the dock should blur. Supplied by the screen that owns the
     * scrolling content underneath; `null` (the default) leaves the dock as a
     * plain translucent pane, which is what it falls back to whenever the
     * user's Blur setting is off.
     */
    backdrop: HazeInput? = null
) {
    if (tabs.isEmpty()) return

    // Everything past the limit collapses into "More" -> the section sheet.
    val visible = tabs.take(maxVisible)
    val overflow = tabs.drop(maxVisible)
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val reducedMotion = rememberReducedMotion()
    val density = LocalDensity.current

    // Where each destination actually landed, in root coordinates.
    //
    // Measured, not computed from the index. The cell widths come from
    // `weight(1f)`, the overflow cell is conditional, and "More" absorbs a
    // different number of destinations on every device -- arithmetic on the
    // index would be wrong on at least one of those, and the bubble would sit
    // half a cell away from the icon it belongs to.
    val itemRects = remember { mutableStateMapOf<String, Rect>() }
    val dockOrigin = remember { mutableStateOf(Offset.Zero) }

    val activeKey = when {
        visible.any { it.id == activeTabId } -> activeTabId
        overflow.any { it.id == activeTabId } -> OVERFLOW_KEY
        // Settings is an action, not a destination, so it never takes the
        // bubble even while its sheet is open.
        else -> null
    }

    val insetX = with(density) { 2.dp.toPx() }
    val insetY = with(density) { 2.dp.toPx() }
    val targetRect = activeKey
        ?.let { itemRects[it] }
        ?.let { r ->
            val origin = dockOrigin.value
            Rect(
                left = r.left - origin.x + insetX,
                top = r.top - origin.y + insetY,
                right = r.right - origin.x - insetX,
                bottom = r.bottom - origin.y - insetY
            )
        }

    // Two independently animated edges, not a position plus a width.
    //
    // That is the whole trick. Animate `left` and `right` separately and the
    // width is whatever the two edges currently are -- so while they travel the
    // capsule *stretches* along the direction of travel and settles back to the
    // cell width. One spring on a position plus one on a width can only ever
    // slide a rigid pill, and a rigid pill is the tell that it is not liquid.
    val leftEdge = remember { Animatable(0f) }
    val rightEdge = remember { Animatable(0f) }
    val topEdge = remember { Animatable(0f) }
    val bottomEdge = remember { Animatable(0f) }
    // First placement snaps. Animating in from (0,0) on first frame would fly
    // the bubble in from the corner of the bar.
    var bubbleSettled by remember { mutableStateOf(false) }

    LaunchedEffect(targetRect, reducedMotion) {
        val t = targetRect ?: return@LaunchedEffect
        if (!bubbleSettled || reducedMotion) {
            leftEdge.snapTo(t.left)
            rightEdge.snapTo(t.right)
            topEdge.snapTo(t.top)
            bottomEdge.snapTo(t.bottom)
            bubbleSettled = true
            return@LaunchedEffect
        }

        val goingRight = t.left > leftEdge.value
        // The edge in the direction of travel leads and arrives first; the
        // trailing edge follows. The capsule is therefore briefly wider than
        // its destination, which is what reads as the material stretching.
        val lead = spring<Float>(dampingRatio = 0.94f, stiffness = 900f)
        val trail = spring<Float>(dampingRatio = 0.88f, stiffness = 520f)
        val vertical = spring<Float>(dampingRatio = 1f, stiffness = Spring.StiffnessMedium)
        coroutineScope {
            launch { leftEdge.animateTo(t.left, if (goingRight) trail else lead) }
            launch { rightEdge.animateTo(t.right, if (goingRight) lead else trail) }
            launch { topEdge.animateTo(t.top, vertical) }
            launch { bottomEdge.animateTo(t.bottom, vertical) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(DockShape)
            .onGloballyPositioned { dockOrigin.value = it.positionInRoot() }
            // The dock is the app's most prominent glass surface, hence
            // strong + raised: full rim, specular sheen and a real shadow.
            //
            // It is also the only surface that asks for the frost pass. Every
            // other pane is a thin sheet over the backdrop; the dock is the one
            // piece of chrome that should read as a solid slab of frosted glass
            // floating over the content, so it gets the milky scatter and the
            // speckle as well.
            .liquidGlass(
                shape = DockShape,
                cornerRadius = 29.dp,
                strong = true,
                raised = true,
                haze = true,
                backdrop = backdrop
            )
    ) {
        // Drawn before the row, so the icons sit on the bubble rather than
        // under it. No pointer input here: the destinations below keep every
        // touch, and the bubble is decoration.
        //
        // Gated on `bubbleSettled` as well, not just on a known target: the
        // springs are still zero until the effect has had its first run, and a
        // zero-sized glass node would be asked to refract nothing. The flag is
        // only ever false before the very first non-null target, so the bubble
        // simply appears already in place instead of measuring itself to 0x0.
        if (bubbleSettled && targetRect != null) {
            Box(
                modifier = Modifier
                    // Deferred reads: the springs run in the layout phase and
                    // never recompose this subtree per frame.
                    .offset {
                        IntOffset(leftEdge.value.roundToInt(), topEdge.value.roundToInt())
                    }
                    .layout { measurable, _ ->
                        val w = (rightEdge.value - leftEdge.value).roundToInt().coerceAtLeast(0)
                        val h = (bottomEdge.value - topEdge.value).roundToInt().coerceAtLeast(0)
                        val placeable = measurable.measure(Constraints.fixed(w, h))
                        layout(w, h) { placeable.place(0, 0) }
                    }
                    .selectionBubble(backdrop = backdrop, isDark = isDark)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
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
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned {
                            val r = it.boundsInRoot()
                            if (itemRects[tab.id] != r) itemRects[tab.id] = r
                        }
                )
            }

            if (overflow.isNotEmpty()) {
                DockItem(
                    label = stringResource(R.string.nav_more),
                    icon = Icons.Filled.MoreHoriz,
                    isActive = overflow.any { it.id == activeTabId },
                    isDark = isDark,
                    onClick = onMoreClick,
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned {
                            val r = it.boundsInRoot()
                            if (itemRects[OVERFLOW_KEY] != r) itemRects[OVERFLOW_KEY] = r
                        }
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
}

/**
 * The sliding selection capsule: Apple's Liquid Glass "bubble".
 *
 * It is glass in its own right rather than a painted pill, which is what makes
 * it read as an object *on* the bar instead of a highlight *of* the bar. It
 * refracts the same backdrop the bar does, so the content behind the bar is
 * bent a second time inside the capsule and the selected destination looks like
 * a lens laid over the bar.
 *
 * The capsule is the raised element, so it is a lighter, crisper glass than the
 * bar it sits on: more specular, less edge shadow, and a brighter veil.
 */
@Composable
private fun Modifier.selectionBubble(backdrop: HazeInput?, isDark: Boolean): Modifier {
    // ~38dp tall and one cell wide, so a 50% corner is a pill at every cell
    // width the bar can produce.
    val shape = RoundedCornerShape(percent = 50)
    val input = backdrop
    return if (input != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this
            .clip(shape)
            .appleLiquidGlass(
                input = input,
                shape = shape,
                cornerRadius = 19.dp,
                tintColor = Color.White.copy(alpha = if (isDark) 0.10f else 0.16f),
                specularIntensity = if (isDark) 0.52f else 0.60f,
                chromaMultiplier = if (isDark) 1.30f else 1.60f,
                edgeShadowAlpha = if (isDark) 0.30f else 0.14f,
                materialAlpha = 1f,
                // Slightly more milk than the bar itself (0.13 dark / 0.20
                // light): the capsule has to read as *brighter* than the bar,
                // not as a clearer window cut into it. A lower veil would make
                // the bubble the sharpest thing on the bar, which the eye reads
                // as a hole rather than as an object sitting on top.
                backgroundVeil = Color.White.copy(alpha = if (isDark) 0.15f else 0.28f),
                ambientLight = if (isDark) 0.32f else 0.46f
            )
    } else {
        // Nothing to refract, or no RenderEffect to refract with (below API 31).
        // A painted translucent capsule still marks the selection.
        this
            .clip(shape)
            .liquidGlass(
                shape = shape,
                cornerRadius = 19.dp,
                strong = false,
                raised = false,
                haze = false
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
 * The active state is carried by the glass bubble sliding underneath this cell,
 * not by anything painted here -- so this cell stays a bare icon plus its touch
 * target, and the bar keeps reading as one object with one moving part.
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

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(DockItemShape)
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
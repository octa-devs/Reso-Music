package com.octadevs.resomusic.ui.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.octadevs.resomusic.ui.theme.NoirPurple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/* ============================================================
   ARTWORK-DERIVED AMBIENT ACCENT
   ------------------------------------------------------------
   The brief allows the player background to "pick up a subtle
   ambient colour from the current album artwork, blended with the dark
   organic background".

   Two deliberate constraints keep this from turning into the cheap
   visualiser look the brief rules out:

   1. It is NOT the artwork's dominant colour. That is usually a loud,
      saturated album-cover hue, and dropping it behind the player makes
      every album look like a nightclub. Instead the artwork only pulls the
      *brand* purple around: the accent is the average artwork colour lerped
      halfway back toward #7959A5. The result is recognisably "this album"
      but always inside the app's palette.

   2. It is averaged, not sampled. One dominant pixel (a bright logo, a
      white border) would swing the whole ambient field.

   No new dependency: Coil is already in the build, and the image is decoded
   at 48px with hardware bitmaps disabled, which is cheap and enough to
   compute a mean. Every failure path resolves to null, leaving the ambient
   layer exactly as it was.
   ============================================================ */

/** Decodes [coverUrl] at a tiny size and returns a blended accent, or null. */
@Composable
fun rememberArtworkAccent(
    coverUrl: Any?,
    /** 0 = pure brand purple, 1 = raw artwork average. */
    artworkWeight: Float = 0.45f
): Color? {
    val context = LocalContext.current
    var accent by remember(coverUrl) { mutableStateOf<Color?>(null) }

    LaunchedEffect(coverUrl) {
        accent = null
        if (coverUrl == null) return@LaunchedEffect

        val bitmap = runCatching {
            val request = ImageRequest.Builder(context)
                .data(coverUrl)
                .size(SAMPLE_PX)
                // A hardware bitmap cannot be read back with getPixels().
                .allowHardware(false)
                .build()
            (context.imageLoader.execute(request) as? SuccessResult)
                ?.drawable
                ?.let { it as? BitmapDrawable }
                ?.bitmap
        }.getOrNull() ?: return@LaunchedEffect

        accent = withContext(Dispatchers.Default) {
            averageColorOf(bitmap)?.let { avg ->
                lerp(NoirPurple, avg, artworkWeight.coerceIn(0f, 1f))
            }
        }
    }

    return accent
}

/** Mean RGB over all visible pixels, or null if there are none. */
private fun averageColorOf(bitmap: Bitmap): Color? {
    val w = bitmap.width
    val h = bitmap.height
    if (w <= 0 || h <= 0) return null

    var r = 0L
    var g = 0L
    var b = 0L
    var n = 0L

    // 16px stride: plenty for a mean, and keeps this to a few hundred reads.
    var y = 0
    while (y < h) {
        var x = 0
        while (x < w) {
            val px = bitmap.getPixel(x, y)
            if (android.graphics.Color.alpha(px) > 128) {
                r += android.graphics.Color.red(px)
                g += android.graphics.Color.green(px)
                b += android.graphics.Color.blue(px)
                n++
            }
            x += SAMPLE_STRIDE
        }
        y += SAMPLE_STRIDE
    }

    if (n == 0L) return null
    return Color(
        red = (r / n).toInt() / 255f,
        green = (g / n).toInt() / 255f,
        blue = (b / n).toInt() / 255f,
        alpha = 1f
    )
}

private const val SAMPLE_PX = 48
private const val SAMPLE_STRIDE = 3

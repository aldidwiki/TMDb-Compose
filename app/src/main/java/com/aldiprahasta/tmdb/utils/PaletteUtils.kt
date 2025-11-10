package com.aldiprahasta.tmdb.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil3.BitmapImage
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import timber.log.Timber

data class PaletteColors(
        val rgbColor: Int,
        val titleTextColor: Int,
        val bodyTextColor: Int
)

/**
 * A Composable function that takes a poster image path, extracts a color Palette,
 * and returns the derived colors for a background (rgbColor), title text, and body text.
 *
 * @param posterPath The path to the image file (e.g., local file path or URI).
 * @return [PaletteColors] containing the calculated rgbColor, titleTextColor, and bodyTextColor.
 */
@Composable
fun rememberPaletteColors(posterPath: String?): PaletteColors {
    val context = LocalContext.current // Access the Android Context

    // 1. State for the generated Palette
    var palette by remember { mutableStateOf<Palette?>(null) }

    // 2. LaunchedEffect to run the side effect (loading image and generating palette)
    LaunchedEffect(posterPath) {
        if (posterPath == null) {
            palette = null // Clear palette if path is null
            return@LaunchedEffect
        }

        // --- Asynchronous Image Loading and Palette Generation ---
        // NOTE: Replace 'context.getImageBitmap(posterPath)' with your actual image loading logic.
        val bitmap = try {
            // Placeholder: Assume this function correctly loads the Bitmap
            context.getImageBitmap(posterPath)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

        bitmap?.let {
            Palette.from(it).generate { p ->
                palette = p // Update the palette state
            }
        }
    }

    // 3. Color Calculation based on the current palette state
    val defaultOnSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()

    val rgbColor = palette?.vibrantSwatch?.rgb
            ?: palette?.dominantSwatch?.rgb
            ?: 0 // Default transparent/black if no swatches found

    val titleTextColor = palette?.vibrantSwatch?.titleTextColor
            ?: palette?.dominantSwatch?.titleTextColor
            ?: defaultOnSurfaceColor // Fallback to MaterialTheme

    val bodyTextColor = palette?.vibrantSwatch?.bodyTextColor
            ?: palette?.dominantSwatch?.bodyTextColor
            ?: defaultOnSurfaceColor // Fallback to MaterialTheme

    // 4. Return the calculated colors
    return PaletteColors(
            rgbColor = rgbColor,
            titleTextColor = titleTextColor,
            bodyTextColor = bodyTextColor
    )
}

private suspend fun Context.getImageBitmap(imagePath: String): Bitmap? {
    val loader = ImageLoader(this)
    val request = ImageRequest.Builder(this)
            .data("https://image.tmdb.org/t/p/w154/$imagePath")
            .allowHardware(false) // Keep this if you need a software bitmap
            .build()

    // Execute the request
    val result = loader.execute(request)

    // Check for success and extract the Image
    return if (result is SuccessResult) {
        // The 'image' property holds the loaded image data
        when (val image = result.image) {
            // BitmapImage is the standard representation for a bitmap-backed image in Coil
            is BitmapImage -> image.bitmap
            // Handle other image types if necessary (e.g., GifImage)
            else -> null
        }
    } else {
        null
    }
}
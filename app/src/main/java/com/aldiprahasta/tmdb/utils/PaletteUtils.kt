package com.aldiprahasta.tmdb.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
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
        val rgbColor: Color,
        val titleTextColor: Color,
        val bodyTextColor: Color
)

/**
 * A Composable function that asynchronously loads an image, extracts a color Palette,
 * and returns the derived colors with a smooth cross-fade animation.
 * * The colors will initially be derived from [MaterialTheme.colorScheme] and smoothly
 * animate to the calculated Palette colors once loading is complete.
 *
 * @param posterPath The URI or URL to the network image file.
 * @return [PaletteColors] containing the animated container (rgbColor), title text, and body text [Color] values.
 */
@Composable
fun rememberPaletteColors(posterPath: String?): PaletteColors {
    val context = LocalContext.current

    // State for the generated Palette
    var palette by remember { mutableStateOf<Palette?>(null) }

    // Define stable default colors using the theme
    val defaultSurfaceColor = MaterialTheme.colorScheme.surface
    val defaultOnSurfaceColor = MaterialTheme.colorScheme.onSurface

    // 1. LaunchedEffect to run the side effect (loading image and generating palette)
    LaunchedEffect(posterPath) {
        if (posterPath == null) {
            palette = null
            return@LaunchedEffect
        }

        val bitmap = try {
            context.getImageBitmap(posterPath)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

        bitmap?.let {
            // Generate Palette asynchronously
            Palette.from(it).generate { p ->
                palette = p
            }
        }
    }

    // 2. Color Calculation: Determine the TARGET colors

    // Background/Container Color
    val targetContainerColor = Color(palette?.vibrantSwatch?.rgb
            ?: palette?.dominantSwatch?.rgb
            ?: defaultSurfaceColor.toArgb()
    )

    // Title Text Color
    val targetTitleColor = Color(palette?.vibrantSwatch?.titleTextColor
            ?: palette?.dominantSwatch?.titleTextColor
            ?: defaultOnSurfaceColor.toArgb()
    )

    // Body Text Color
    val targetBodyColor = Color(palette?.vibrantSwatch?.bodyTextColor
            ?: palette?.dominantSwatch?.bodyTextColor
            ?: defaultOnSurfaceColor.toArgb()
    )

    // 3. Animation Logic

    // Check if the color is still the default fallback. If so, use the fallback as the target,
    // ensuring the animation only starts when a new palette color arrives.
    val actualTargetContainerColor = if (targetContainerColor == defaultSurfaceColor) defaultSurfaceColor else targetContainerColor
    val actualTargetTitleColor = if (targetTitleColor == defaultOnSurfaceColor) defaultOnSurfaceColor else targetTitleColor
    val actualTargetBodyColor = if (targetBodyColor == defaultOnSurfaceColor) defaultOnSurfaceColor else targetBodyColor

    val animatedContainerColor by animateColorAsState(
            targetValue = actualTargetContainerColor,
            animationSpec = tween(durationMillis = 400, delayMillis = 100), // Adjusted to 400ms duration for better visibility
            label = "AnimatedContainerColor"
    )

    val animatedTitleColor by animateColorAsState(
            targetValue = actualTargetTitleColor,
            animationSpec = tween(durationMillis = 400, delayMillis = 100),
            label = "AnimatedTitleColor"
    )

    val animatedBodyColor by animateColorAsState(
            targetValue = actualTargetBodyColor,
            animationSpec = tween(durationMillis = 400, delayMillis = 100),
            label = "AnimatedBodyColor"
    )

    // 4. Return the animated colors
    return PaletteColors(
            rgbColor = animatedContainerColor,
            titleTextColor = animatedTitleColor,
            bodyTextColor = animatedBodyColor
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
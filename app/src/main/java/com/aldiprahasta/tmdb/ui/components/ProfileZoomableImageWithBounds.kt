package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlin.math.max

/**
 * A custom [Saver] for the [Offset] class.
 * This allows the [Offset] state variable to be correctly saved and restored
 * across configuration changes (e.g., screen rotation) when used with [rememberSaveable].
 *
 * Saves the Offset as a List of two Floats (x and y).
 */
private val OffsetSaver = Saver<Offset, List<Float>>(
        // Saves the x and y coordinates of the Offset into a list
        save = { listOf(it.x, it.y) },
        // Restores the Offset from the saved list
        restore = { Offset(it[0], it[1]) }
)

/**
 * Clamps the image translation offset to ensure the scaled image does not pan
 * outside the visible viewport boundaries.
 *
 * @param newOffset The proposed new offset from a pan gesture or double-tap calculation.
 * @param scale The current scale factor of the image.
 * @param viewport The size of the visible Composable container (the area the image is displayed in).
 * @return A new [Offset] that is constrained within the allowed boundaries.
 */
private fun clampOffset(newOffset: Offset, scale: Float, viewport: Size): Offset {
    // If the image is not zoomed in (scale <= 1f) or the viewport size is unknown,
    // the offset must be zero (center aligned).
    if (scale <= 1f || viewport == Size.Zero) return Offset.Zero

    // 1. Calculate the actual size of the content after scaling.
    // The scaled size is the viewport size multiplied by the scale factor.
    val scaledContentWidth = viewport.width * scale
    val scaledContentHeight = viewport.height * scale

    // 2. Calculate the "overflow" size.
    // This is the part of the image that extends beyond the visible viewport.
    // We use max(0f, ...) to ensure the value is not negative (though it shouldn't be here).
    val overflowX = max(0f, scaledContentWidth - viewport.width)
    val overflowY = max(0f, scaledContentHeight - viewport.height)

    // 3. Determine the maximum allowed translation.
    // Since panning is centered, the max translation is half of the total overflow.
    // E.g., if the image is 200px wider than the viewport, you can pan 100px left and 100px right.
    val maxOffsetX = overflowX / 2f
    val maxOffsetY = overflowY / 2f

    // 4. Clamp the new offset.
    // This ensures the translation (newOffset.x/y) never exceeds the maximum allowed offset.
    // The range is [-maxOffset, +maxOffset].
    return Offset(
            x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
            y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
    )
}

/**
 * A Composable function that displays a profile image with zoom (pinch) and pan (drag)
 * capabilities. It supports double-tap to zoom/reset.
 *
 * @param imagePath The path (e.g., URL or local URI) for the image to display.
 * @param minScale The minimum allowed scale factor (default is 1f, no zoom).
 * @param maxScale The maximum allowed scale factor (default is 4f).
 * @param modifier The [Modifier] to be applied to the image container.
 */
@Composable
fun ProfileZoomableImageWithBounds(
        imagePath: String?,
        minScale: Float = 1f,
        maxScale: Float = 4f,
        modifier: Modifier
) {
    // Stores the current scale factor of the image. Saved across configuration changes.
    var scale by rememberSaveable { mutableFloatStateOf(minScale) }
    // Stores the current translation offset (pan) of the image. Saved across config changes.
    var offset by rememberSaveable(stateSaver = OffsetSaver) { mutableStateOf(Offset.Zero) }

    // Stores the current size of the Composable container in which the image is displayed.
    // Used for clamping the pan offset to prevent panning outside the image boundaries.
    var imageDisplaySize by remember { mutableStateOf(Size.Zero) }

    // Placeholder Composable for an Image Loader function (e.g., Coil/Glide/etc.)
    ImageLoader(
            imagePath = imagePath,
            imageType = ImageType.PROFILE,
            modifier = modifier
                    // Enforces a standard profile aspect ratio (e.g., for full-screen view)
                    .aspectRatio(9f / 16f)
                    // Updates imageDisplaySize state when the layout size changes
                    .onSizeChanged { imageDisplaySize = it.toSize() }
                    .clip(RoundedCornerShape(8.dp))
                    // Applies the scale and offset transform using GraphicsLayer for performance
                    .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                    )
                    // Pinch/Pan Gesture Detection (for simultaneous zooming and panning)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            // --- 1. Update Scale (Zoom) ---
                            // Apply new zoom factor and constrain it within minScale and maxScale
                            val newScale = (scale * zoom).coerceIn(minScale, maxScale)
                            scale = newScale

                            // --- 2. Update Offset (Pan) ---
                            if (scale > minScale) {
                                // Only allow panning when zoomed in
                                val newOffset = offset + pan

                                // Clamps the offset so the image edges stay within the viewport
                                offset = clampOffset(newOffset, newScale, imageDisplaySize)
                            } else {
                                // Reset offset to center when fully zoomed out
                                offset = Offset.Zero
                            }
                        }
                    }
                    // Double-Tap Gesture Detection (for quick zoom/reset)
                    // Key on imageDisplaySize: ensures proper coordinate mapping if the size changes.
                    .pointerInput(imageDisplaySize) {
                        detectTapGestures(onDoubleTap = { tapOffset ->
                            // Determine the target scale:
                            // If currently zoomed in, reset to minScale.
                            // Otherwise, zoom to a default value (e.g., maxScale / 2).
                            val targetScale = if (scale > minScale) minScale else maxScale / 2f

                            // 1. Calculate the change in scale
                            val zoomRatio = targetScale / scale

                            // 2. Determine the required offset shift to center the tap point
                            // Calculate the delta: (tap point relative to center) * (zoom ratio - 1)
                            // This finds how much the view shifts due to scaling around the center,
                            // and adjusts the translation to keep the tap location in place after scaling.
                            val newOffsetX = (tapOffset.x - imageDisplaySize.width / 2f) * (zoomRatio - 1)
                            val newOffsetY = (tapOffset.y - imageDisplaySize.height / 2f) * (zoomRatio - 1)

                            // 3. Apply the calculated shift to the current offset
                            val calculatedOffset = Offset(
                                    x = offset.x - newOffsetX,
                                    y = offset.y - newOffsetY
                            )

                            // Update scale first
                            scale = targetScale

                            // 4. Clamp the resulting offset using the new scale and viewport size
                            offset = if (targetScale > minScale) {
                                // Clamp the offset to ensure boundaries are respected
                                clampOffset(calculatedOffset, targetScale, imageDisplaySize)
                            } else {
                                // If resetting, ensure offset is Zero
                                Offset.Zero
                            }
                        })
                    }
    )
}
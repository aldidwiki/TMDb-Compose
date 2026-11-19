package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size

internal class PhotoZoomState {
    var scale by mutableFloatStateOf(1f)
        private set
    var offset by mutableStateOf(Offset.Zero)
        private set

    fun transform(centroid: Offset, pan: Offset, zoom: Float, viewport: IntSize) {
        val targetScale = (scale * zoom).coerceIn(1f, 4f)
        val ratio = targetScale / scale
        val anchor = centroid - Offset(viewport.width / 2f, viewport.height / 2f)
        val targetOffset = anchor + (offset - anchor) * ratio + pan
        val limitX = viewport.width * (targetScale - 1f) / 2f
        val limitY = viewport.height * (targetScale - 1f) / 2f

        scale = targetScale
        offset = Offset(targetOffset.x.coerceIn(-limitX, limitX), targetOffset.y.coerceIn(-limitY, limitY))
    }
}

@Composable
internal fun ZoomablePhoto(
        imagePath: String,
        thumbnailType: ImageType,
        zoomState: PhotoZoomState,
        zoomEnabled: Boolean,
        zoomFraction: Float,
        modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val original = remember(context, imagePath, thumbnailType) {
        ImageRequest.Builder(context)
                .data("https://image.tmdb.org/t/p/original/${imagePath.trimStart('/')}")
                .size(if (thumbnailType == ImageType.BACKDROP) Size(4096, 4096) else Size.ORIGINAL)
                .crossfade(true)
                .build()
    }

    Box(
            modifier = modifier.clipToBounds().pointerInput(zoomState, zoomEnabled) {
                if (zoomEnabled) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        zoomState.transform(centroid, pan, zoom, size)
                    }
                }
            }
    ) {
        Box(
                modifier = Modifier.matchParentSize().graphicsLayer {
                    val scale = 1f + (zoomState.scale - 1f) * zoomFraction
                    scaleX = scale
                    scaleY = scale
                    translationX = zoomState.offset.x * zoomFraction
                    translationY = zoomState.offset.y * zoomFraction
                }
        ) {
            MovieArtwork(imagePath, thumbnailType, Modifier.matchParentSize())
            AsyncImage(
                    model = original,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
            )
        }
    }
}

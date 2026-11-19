package com.aldiprahasta.tmdb.ui.image

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.PhotoZoomState
import com.aldiprahasta.tmdb.ui.components.ZoomablePhoto
import kotlin.math.roundToInt

@Composable
internal fun PhotoViewer(
        imagePath: String,
        aspectRatio: Float?,
        thumbnail: Rect,
        caption: String,
        isBackdrop: Boolean,
        onClosed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    val zoomFraction = remember { Animatable(1f) }
    val zoomState = remember { PhotoZoomState() }
    var closing by remember { mutableStateOf(false) }
    val requestClose = { closing = true }
    val title = stringResource(R.string.gallery_viewer_title)
    val imageDescription = stringResource(R.string.gallery_viewer_image)
    val zoomPercentage = (100 * (1f + (zoomState.scale - 1f) * zoomFraction.value)).roundToInt()
    val zoomDescription = stringResource(R.string.gallery_zoom, zoomPercentage)
    val closeLabel = stringResource(R.string.discover_close)
    val zoomEnabled = !closing && progress.value == 1f

    BackHandler(onBack = requestClose)

    LaunchedEffect(closing) {
        if (closing) {
            if (zoomState.scale > 1f) {
                zoomFraction.animateTo(0f, tween(180, easing = FastOutSlowInEasing))
            }
            progress.animateTo(0f, tween(340, easing = FastOutSlowInEasing))
            onClosed()
        } else {
            progress.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
        }
    }

    BoxWithConstraints(modifier = modifier.semantics { paneTitle = title }) {
        val density = LocalDensity.current
        val safeDrawing = WindowInsets.safeDrawing
        val imageType = if (isBackdrop) ImageType.BACKDROP else ImageType.PROFILE
        val fallbackAspect = if (isBackdrop) imageType.aspectRatio else 2f / 3f
        val aspect = aspectRatio?.takeIf { it.isFinite() && it > 0f } ?: fallbackAspect
        val captionStyle = MaterialTheme.typography.labelLarge
        val horizontalInset = if (isBackdrop) 20.dp else 28.dp
        val fit = with(density) {
            val rootWidth = maxWidth.toPx()
            val rootHeight = maxHeight.toPx()
            val top = safeDrawing.getTop(this) + 64.dp.toPx()
            val captionHeight = if (isBackdrop) 2 * captionStyle.lineHeight.toPx() + 8.dp.toPx() else 0f
            val bottom = rootHeight - safeDrawing.getBottom(this) - maxOf(48.dp.toPx(), captionHeight)
            val availableHeight = (bottom - top).coerceAtLeast(1f)
            val availableWidth = (rootWidth - 2 * horizontalInset.toPx()).coerceAtLeast(1f)
            val width = minOf(availableWidth, availableHeight * aspect)
            val height = width / aspect
            val left = (rootWidth - width) / 2
            val topOfPhoto = top + (availableHeight - height) / 2
            Rect(left, topOfPhoto, left + width, topOfPhoto + height)
        }
        val bounds = lerp(thumbnail, fit, progress.value)
        val shape = RoundedCornerShape(12.dp)

        Box(
                modifier = Modifier.matchParentSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.56f * progress.value))
                        .clickable(
                                interactionSource = null,
                                indication = null,
                                onClickLabel = closeLabel,
                                onClick = requestClose
                        )
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ -> change.consume() }
                        }
        )

        ZoomablePhoto(
                imagePath = imagePath,
                thumbnailType = imageType,
                zoomState = zoomState,
                zoomEnabled = zoomEnabled,
                zoomFraction = zoomFraction.value,
                modifier = Modifier.offset { IntOffset(bounds.left.roundToInt(), bounds.top.roundToInt()) }
                        .requiredSize(with(density) { bounds.width.toDp() }, with(density) { bounds.height.toDp() })
                        .clip(shape)
                        .semantics {
                            contentDescription = imageDescription
                            stateDescription = zoomDescription
                        }
                        .pointerInput(Unit) { detectTapGestures(onTap = {}) }
        )

        FilledIconButton(
                onClick = requestClose,
                modifier = Modifier.align(Alignment.TopEnd)
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.End))
                        .padding(top = 8.dp, end = 20.dp)
                        .alpha(progress.value),
                colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                )
        ) {
            Icon(Icons.Filled.Close, contentDescription = closeLabel)
        }

        if (caption.isNotBlank()) {
            val captionModifier = if (isBackdrop) {
                Modifier.align(Alignment.TopCenter)
                        .offset { IntOffset(0, fit.bottom.roundToInt()) }
                        .width(with(density) { fit.width.toDp() })
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                        .alpha(((progress.value - 0.7f) / 0.3f).coerceIn(0f, 1f))
            } else {
                Modifier.align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                        .padding(horizontal = horizontalInset, vertical = 14.dp)
                        .alpha(progress.value)
            }

            Text(
                    text = caption,
                    textAlign = if (isBackdrop) TextAlign.Start else TextAlign.Center,
                    style = captionStyle,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = if (isBackdrop) 2 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis,
                    modifier = captionModifier
            )
        }
    }
}

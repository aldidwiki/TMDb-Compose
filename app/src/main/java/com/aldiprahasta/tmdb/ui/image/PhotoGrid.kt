package com.aldiprahasta.tmdb.ui.image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.ui.components.ShimmerEffect
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview

@Composable
internal fun PhotoGrid(
        photos: List<ImageDomainModel>,
        isBackdrop: Boolean,
        selectedPath: String?,
        onPhotoClicked: (ImageDomainModel, Int, Rect) -> Unit
) {
    LazyVerticalGrid(
            columns = GridCells.Fixed(if (isBackdrop) 2 else 3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = selectedPath == null
    ) {
        itemsIndexed(photos, key = { index, photo -> photo.filePath ?: index }) { index, photo ->
            var bounds by remember { mutableStateOf(Rect.Zero) }
            val description = stringResource(
                    if (isBackdrop) R.string.gallery_open_backdrop else R.string.person_photo_open,
                    index + 1
            )

            MovieArtwork(
                    imagePath = photo.filePath,
                    imageType = if (isBackdrop) ImageType.BACKDROP else ImageType.PROFILE,
                    modifier = Modifier.aspectRatio(thumbnailAspectRatio(isBackdrop))
                            .onGloballyPositioned { bounds = it.boundsInRoot() }
                            .alpha(if (photo.filePath == selectedPath) 0f else 1f)
                            .clip(RoundedCornerShape(10.dp))
                            .semantics { contentDescription = description }
                            .clickable(
                                    enabled = selectedPath == null,
                                    role = Role.Button,
                                    onClick = {
                                        if (bounds.width > 0f && bounds.height > 0f) onPhotoClicked(photo, index, bounds)
                                    }
                            )
            )
        }
    }
}

@Composable
internal fun PhotoGridLoading(isBackdrop: Boolean) {
    LazyVerticalGrid(
            columns = GridCells.Fixed(if (isBackdrop) 2 else 3),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
    ) {
        items(6) {
            ShimmerEffect(Modifier.aspectRatio(thumbnailAspectRatio(isBackdrop)).clip(RoundedCornerShape(10.dp)))
        }
    }
}

private fun thumbnailAspectRatio(isBackdrop: Boolean): Float = if (isBackdrop) ImageType.BACKDROP.aspectRatio else 9f / 16f

@Preview(widthDp = 390, heightDp = 332)
@Composable
private fun PhotoGridPreview() {
    DiscoverPreview {
        PhotoGrid(
                photos = List(5) { ImageDomainModel(1, "/preview-backdrop-$it.jpg", 16f / 9f) },
                isBackdrop = true,
                selectedPath = null,
                onPhotoClicked = { _, _, _ -> }
        )
    }
}

@Preview(widthDp = 390, heightDp = 324)
@Composable
private fun PhotoGridLoadingPreview() {
    DiscoverPreview {
        PhotoGridLoading(isBackdrop = false)
    }
}

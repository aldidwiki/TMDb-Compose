package com.aldiprahasta.tmdb.ui.image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.ui.components.ShimmerEffect
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.DiscoverTopBar
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ImageGalleryScreen(
        contentName: String,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: ImageViewModel = koinViewModel()
    val images by viewModel.images.collectAsStateWithLifecycle()

    DiscoverSystemBars()
    DiscoverTheme {
        PhotoGalleryContent(
                personName = contentName,
                images = images,
                onBackPressed = onBackPressed,
                onRetry = viewModel::retry,
                modifier = modifier
        )
    }
}

@Composable
private fun PhotoGalleryContent(
        personName: String,
        images: UiState<List<ImageDomainModel>>,
        onBackPressed: () -> Unit,
        onRetry: () -> Unit,
        modifier: Modifier = Modifier
) {
    var origin by remember { mutableStateOf(Offset.Zero) }
    var selection by remember { mutableStateOf<SelectedPhoto?>(null) }
    val photos = remember(images) {
        (images as? UiState.Success)?.data.orEmpty()
                .filter { !it.filePath.isNullOrBlank() }
                .distinctBy { it.filePath }
    }

    Box(modifier = modifier.fillMaxSize().onGloballyPositioned { origin = it.positionInRoot() }) {
        Scaffold(
                modifier = Modifier.fillMaxSize().semantics {
                    if (selection != null) hideFromAccessibility()
                },
                containerColor = MaterialTheme.colorScheme.background,
                topBar = { DiscoverTopBar(stringResource(R.string.person_photos_title), onBackPressed) }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (personName.isNotBlank()) {
                        Text(personName, style = MaterialTheme.typography.headlineSmall)
                    }
                    if (images is UiState.Success) {
                        Text(
                                text = pluralStringResource(R.plurals.person_photo_count, photos.size, photos.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                when (images) {
                    UiState.Loading -> PhotoGridLoading()
                    is UiState.Error -> DiscoverStatus(
                            message = stringResource(R.string.person_photos_error),
                            onRetry = onRetry,
                            modifier = Modifier.fillMaxSize()
                    )
                    is UiState.Success -> if (photos.isEmpty()) {
                        DiscoverStatus(stringResource(R.string.person_no_photos), Modifier.fillMaxSize())
                    } else {
                        PhotoGrid(
                                photos = photos,
                                selectedPath = selection?.imagePath,
                                onPhotoClicked = { photo, bounds ->
                                    val path = photo.filePath
                                    if (path != null && selection == null) {
                                        selection = SelectedPhoto(
                                                imagePath = path,
                                                aspectRatio = photo.aspectRatio,
                                                thumbnail = bounds.translate(-origin)
                                        )
                                    }
                                }
                        )
                    }
                }
            }
        }

        selection?.let { photo ->
            key(photo.imagePath) {
                PersonPhotoViewer(
                        imagePath = photo.imagePath,
                        aspectRatio = photo.aspectRatio,
                        thumbnail = photo.thumbnail,
                        personName = personName,
                        onClosed = { selection = null },
                        modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}

@Composable
private fun PhotoGrid(
        photos: List<ImageDomainModel>,
        selectedPath: String?,
        onPhotoClicked: (ImageDomainModel, Rect) -> Unit
) {
    LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = selectedPath == null
    ) {
        itemsIndexed(photos, key = { _, photo -> photo.filePath!! }) { index, photo ->
            var bounds by remember { mutableStateOf(Rect.Zero) }
            val description = stringResource(R.string.person_photo_open, index + 1)

            MovieArtwork(
                    imagePath = photo.filePath,
                    imageType = ImageType.PROFILE,
                    modifier = Modifier.aspectRatio(9f / 16f)
                            .onGloballyPositioned { bounds = it.boundsInRoot() }
                            .alpha(if (photo.filePath == selectedPath) 0f else 1f)
                            .clip(RoundedCornerShape(10.dp))
                            .semantics { contentDescription = description }
                            .clickable(
                                    enabled = selectedPath == null,
                                    role = Role.Button,
                                    onClick = {
                                        if (bounds.width > 0f && bounds.height > 0f) onPhotoClicked(photo, bounds)
                                    }
                            )
            )
        }
    }
}

@Composable
private fun PhotoGridLoading() {
    LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
    ) {
        items(6) {
            ShimmerEffect(Modifier.aspectRatio(9f / 16f).clip(RoundedCornerShape(10.dp)))
        }
    }
}

private data class SelectedPhoto(val imagePath: String, val aspectRatio: Float?, val thumbnail: Rect)

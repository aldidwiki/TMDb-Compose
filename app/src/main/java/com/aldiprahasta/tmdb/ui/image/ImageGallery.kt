package com.aldiprahasta.tmdb.ui.image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.DiscoverTopBar
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.MediaType
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

    DiscoverTheme {
        PhotoGalleryContent(
                contentName = contentName,
                isBackdrop = viewModel.mediaType != MediaType.PERSON_TYPE,
                images = images,
                onBackPressed = onBackPressed,
                onRetry = viewModel::retry,
                modifier = modifier
        )
    }
}

@Composable
private fun PhotoGalleryContent(
        contentName: String,
        isBackdrop: Boolean,
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
                topBar = { DiscoverTopBar(stringResource(R.string.gallery_title), onBackPressed) }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (contentName.isNotBlank()) {
                        Text(contentName, style = MaterialTheme.typography.headlineSmall)
                    }
                    if (images is UiState.Success) {
                        Text(
                                text = pluralStringResource(
                                        if (isBackdrop) R.plurals.gallery_backdrop_count else R.plurals.person_photo_count,
                                        photos.size,
                                        photos.size
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                when (images) {
                    UiState.Loading -> PhotoGridLoading(isBackdrop)
                    is UiState.Error -> DiscoverStatus(
                            message = stringResource(R.string.gallery_load_error),
                            onRetry = onRetry,
                            modifier = Modifier.fillMaxSize()
                    )
                    is UiState.Success -> if (photos.isEmpty()) {
                        val emptyMessage = if (isBackdrop) R.string.gallery_no_backdrops else R.string.person_no_photos
                        DiscoverStatus(stringResource(emptyMessage), Modifier.fillMaxSize())
                    } else {
                        PhotoGrid(
                                photos = photos,
                                isBackdrop = isBackdrop,
                                selectedPath = selection?.imagePath,
                                onPhotoClicked = { photo, index, bounds ->
                                    val path = photo.filePath
                                    if (path != null && selection == null) {
                                        selection = SelectedPhoto(
                                                imagePath = path,
                                                aspectRatio = photo.aspectRatio,
                                                thumbnail = bounds.translate(-origin),
                                                index = index
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
                val caption = when {
                    !isBackdrop -> contentName
                    contentName.isBlank() -> stringResource(R.string.gallery_image_position, photo.index + 1, photos.size)
                    else -> stringResource(R.string.gallery_backdrop_caption, contentName, photo.index + 1, photos.size)
                }
                PhotoViewer(
                        imagePath = photo.imagePath,
                        aspectRatio = photo.aspectRatio,
                        thumbnail = photo.thumbnail,
                        caption = caption,
                        isBackdrop = isBackdrop,
                        onClosed = { selection = null },
                        modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}

private data class SelectedPhoto(val imagePath: String, val aspectRatio: Float?, val thumbnail: Rect, val index: Int)

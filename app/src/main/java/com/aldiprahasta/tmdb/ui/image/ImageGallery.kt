package com.aldiprahasta.tmdb.ui.image

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.ImageLoader
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.ProfileZoomableImageWithBounds
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import com.aldiprahasta.tmdb.utils.extractNameByWordCount
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageGalleryScreen(
        contentId: Int,
        contentName: String,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: ImageViewModel = koinViewModel()

    viewModel.setContentId(contentId)
    val imageDomainModel by viewModel.images.collectAsStateWithLifecycle()

    var isPreviewDialogVisible by remember { mutableStateOf(false) }
    var imageCount by remember { mutableIntStateOf(0) }
    val imageTitle = pluralStringResource(R.plurals.image, imageCount)

    val blurRadius = if (isPreviewDialogVisible) 6.dp else 0.dp

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text(text = "${extractNameByWordCount(contentName)}'s $imageTitle")
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                                scrolledContainerColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = Color.White,
                        ),
                        navigationIcon = {
                            IconButton(onClick = onBackPressed) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        tint = Color.White,
                                        contentDescription = null
                                )
                            }
                        }
                )
            },
            modifier = modifier
                    .blur(blurRadius)
    ) { innerPadding ->
        AnimatedContent(
                targetState = imageDomainModel,
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(tween(500))
                },
                modifier = Modifier.padding(innerPadding)
        ) { state ->
            state.doIfLoading {
                LoadingScreen()
            }

            state.doIfError { throwable, errorMessage ->
                ErrorScreen()
            }

            state.doIfSuccess { images ->
                imageCount = images.size
                ImageGalleryList(
                        images = images,
                        onPreviewDialogChange = { isPreviewDialogVisible = it },
                )
            }
        }
    }
}

@Composable
fun ImageGalleryList(
        images: List<ImageDomainModel>,
        onPreviewDialogChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
) {
    var isPreviewVisible by remember { mutableStateOf(false) }
    var selectedImagePath by remember { mutableStateOf<String?>(null) }

    val dismissPreview: () -> Unit = {
        isPreviewVisible = false
        selectedImagePath = null
    }

    onPreviewDialogChange(isPreviewVisible)

    LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = modifier.fillMaxSize()
    ) {
        items(
                items = images
        ) { image ->
            ImageLoader(
                    imagePath = image.filePath,
                    imageType = ImageType.PROFILE,
                    modifier = Modifier
                            .aspectRatio(ImageType.PROFILE.aspectRatio)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                isPreviewVisible = true
                                selectedImagePath = image.filePath
                            }
            )
        }
    }

    if (isPreviewVisible && selectedImagePath != null) {
        ImagePreviewDialog(
                imagePath = selectedImagePath,
                onDismissRequest = dismissPreview,
        )
    }
}

@Composable
fun ImagePreviewDialog(
        imagePath: String?,
        onDismissRequest: () -> Unit,
        modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest) {
        ProfileZoomableImageWithBounds(
                imagePath = imagePath,
                modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageGalleryScreenPreview() {
    ImageGalleryList(
            listOf(
                    ImageDomainModel(
                            id = 0,
                            filePath = "/snk6JiXOOoRjPtHU5VMoy6qbd32.jpg"
                    ),
                    ImageDomainModel(
                            id = 0,
                            filePath = "/snk6JiXOOoRjPtHU5VMoy6qbd32.jpg"
                    ),
                    ImageDomainModel(
                            id = 0,
                            filePath = "/snk6JiXOOoRjPtHU5VMoy6qbd32.jpg"
                    ),
                    ImageDomainModel(
                            id = 0,
                            filePath = "/snk6JiXOOoRjPtHU5VMoy6qbd32.jpg"
                    ),
                    ImageDomainModel(
                            id = 0,
                            filePath = "/snk6JiXOOoRjPtHU5VMoy6qbd32.jpg"
                    ),
            ),
            onPreviewDialogChange = {}
    )
}
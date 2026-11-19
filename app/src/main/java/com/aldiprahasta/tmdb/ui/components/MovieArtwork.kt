package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.aldiprahasta.tmdb.R

@Composable
fun MovieArtwork(
        imagePath: String?,
        imageType: ImageType,
        modifier: Modifier = Modifier,
        placeholder: @Composable (Modifier) -> Unit = { ArtworkPlaceholder(it) }
) {
    if (imagePath.isNullOrBlank()) {
        placeholder(modifier)
        return
    }

    SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                    .data("https://image.tmdb.org/t/p/${imageType.size}/${imagePath.trimStart('/')}")
                    .crossfade(true)
                    .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = { ShimmerEffect(Modifier.fillMaxSize()) },
            error = { placeholder(Modifier.fillMaxSize()) },
            modifier = modifier
    )
}

@Composable
private fun ArtworkPlaceholder(modifier: Modifier = Modifier) {
    Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Image(
                painter = painterResource(R.drawable.ic_broken_image),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
        )
    }
}

package com.aldiprahasta.tmdb.ui.components

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ImageLoader(
        imagePath: String?,
        imageType: ImageType,
        modifier: Modifier = Modifier
) {
    AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                    .data("https://image.tmdb.org/t/p/${imageType.value}/$imagePath")
                    .placeholder(ColorDrawable(Color.GRAY))
                    .crossfade(true)
                    .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier
                    .clip(RoundedCornerShape(6.dp))
                    .width(100.dp)
                    .height(150.dp)
    )
}

@Composable
fun ErrorScreen(errorMessage: String, modifier: Modifier = Modifier) {
    Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize()
    ) {
        Text(
                text = errorMessage,
                style = MaterialTheme.typography.displaySmall
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(Modifier.size(56.dp))
    }
}

enum class ImageType(val value: String) {
    POSTER("w500"),
    BACKDROP("w780")
}
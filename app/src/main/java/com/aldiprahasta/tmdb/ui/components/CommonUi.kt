package com.aldiprahasta.tmdb.ui.components

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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

enum class ImageType(val value: String) {
    POSTER("w500"),
    BACKDROP("w780")
}
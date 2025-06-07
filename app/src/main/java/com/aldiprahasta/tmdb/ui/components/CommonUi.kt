package com.aldiprahasta.tmdb.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aldiprahasta.tmdb.R

@Composable
fun ImageLoader(
        imagePath: String?,
        imageType: ImageType,
        modifier: Modifier = Modifier
) {
    AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                    .data("https://image.tmdb.org/t/p/${imageType.size}/$imagePath")
                    .placeholder(Color.GRAY.toDrawable())
                    .error(R.drawable.ic_broken_image)
                    .crossfade(true)
                    .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier
    )
}

@Composable
fun ImageLoaderBackdrop(
        imagePath: String,
        modifier: Modifier = Modifier
) {
    var isError by remember { mutableStateOf(false) }
    if (!isError) AsyncImage(
            model = "https://image.tmdb.org/t/p/${ImageType.BACKDROP.size}/$imagePath",
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            onError = { isError = true },
            modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp)
    )
}

@Composable
fun ErrorScreen(
        modifier: Modifier = Modifier,
        errorMessage: String? = null
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.error_animation))

    Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize()
    ) {
        errorMessage?.let { message ->
            Text(
                    textAlign = TextAlign.Center,
                    text = message,
                    style = MaterialTheme.typography.displaySmall
            )
        } ?: run {
            LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(250.dp)
            )
        }
    }
}

@Composable
fun LoadingScreen(
        modifier: Modifier = Modifier,
        indicatorSizeInDp: Dp = 56.dp
) {
    Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(Modifier.size(indicatorSizeInDp))
    }
}

@Composable
fun PagingErrorFooter(
        errorMessage: String?,
        onRetryClicked: () -> Unit,
        modifier: Modifier = Modifier
) {
    Row(
            modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
                text = errorMessage ?: "No Data Found",
                modifier = Modifier.weight(1f)
        )

        TextButton(onClick = onRetryClicked) {
            Text(text = "Retry")
        }
    }
}

enum class ImageType(val size: String) {
    POSTER("w500"),
    BACKDROP("w780"),
    PROFILE("h632"),
    LOGO("w185"),
    STILL("w780")
}
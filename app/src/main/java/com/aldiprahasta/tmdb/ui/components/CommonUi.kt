package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aldiprahasta.tmdb.R

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

enum class ImageType(val size: String, val aspectRatio: Float) {
    POSTER("w500", (3f / 4f)),
    BACKDROP("w780", (16f / 9f)),
    PROFILE("h632", (3f / 4f)),
    STILL("w780", (16f / 9f))
}
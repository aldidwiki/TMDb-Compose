package com.aldiprahasta.tmdb.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun ShimmerEffect(modifier: Modifier = Modifier) {
    // 1. Define the animation values
    val transition = rememberInfiniteTransition(label = "shimmerTransition")

    // Animate the float value from 0f to 1000f over 1 second
    val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f, // Controls the speed and distance of the shimmer
            animationSpec = infiniteRepeatable(
                    animation = tween(
                            durationMillis = 1000, // Duration of one cycle
                            easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
            ),
            label = "shimmerTranslate"
    )

    // 2. Define the shimmer colors
    val shimmerColorShades = listOf(
            Color.LightGray.copy(alpha = 0.3f), // Start/End
            Color.White.copy(alpha = 0.6f),    // Middle/Shine
            Color.LightGray.copy(alpha = 0.3f) // Start/End
    )

    // 3. Create the linear gradient brush
    val brush = Brush.linearGradient(
            colors = shimmerColorShades,
            start = Offset(translateAnim, translateAnim),
            end = Offset(translateAnim + 100, translateAnim + 100)
    )

    // 4. Apply the gradient background to the container
    Spacer(modifier = modifier.background(brush))
}
package com.aldiprahasta.tmdb.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun LazyColumnWithScrollbar(
        modifier: Modifier = Modifier,
        contentPadding: PaddingValues = PaddingValues(0.dp),
        content: LazyListScope.() -> Unit
) {
    val listState = rememberLazyListState()
    var trackHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    // 1. DYNAMIC CALCULATIONS
    val thumbProperties by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount

            if (totalItems == 0 || layoutInfo.viewportSize.height == 0) {
                return@derivedStateOf Pair(0f, 0f) // (scrollProgress, thumbSizeRatio)
            }

            // Calculate Scroll Progress (same as before, approx position)
            val maxScrollIndex = totalItems - layoutInfo.visibleItemsInfo.size.coerceAtLeast(1)
            val currentScrollIndex = listState.firstVisibleItemIndex.toFloat() +
                    listState.firstVisibleItemScrollOffset.toFloat() /
                    layoutInfo.visibleItemsInfo.first().size.toFloat().coerceAtLeast(1f)

            val scrollProgress = (currentScrollIndex / maxScrollIndex.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)

            // Calculate Thumb Size Ratio: (Viewport Height / Total Content Height)
            // Note: Total content height is approximated by (item count * average item size).
            val averageItemSize = layoutInfo.visibleItemsInfo.lastOrNull()?.size ?: 0
            val estimatedContentHeight = totalItems * averageItemSize

            // This ratio determines the height of the thumb relative to the track height.
            val thumbSizeRatio = (layoutInfo.viewportSize.height.toFloat() / estimatedContentHeight.toFloat())
                    .coerceIn(0f, 1f) // Ensure it stays between 0% and 100%

            Pair(scrollProgress, thumbSizeRatio)
        }
    }

    val (scrollProgress, thumbSizeRatio) = thumbProperties

    // Animate the alpha (visibility) of the scrollbar when scrolling starts/stops
    val targetAlpha = if (listState.isScrollInProgress) 1f else 0f
    val duration = if (listState.isScrollInProgress) 150 else 500
    val alpha by animateFloatAsState(
            targetValue = targetAlpha,
            animationSpec = tween(durationMillis = duration),
            label = "Scrollbar Alpha Animation"
    )

    // 2. BOX LAYOUT FOR LAYERING
    Box(modifier = modifier.fillMaxSize()) {

        // --- LAZY COLUMN (The main content) ---
        LazyColumn(
                state = listState,
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize(),
                content = content
        )

        // --- CUSTOM SCROLLBAR INDICATOR ---
        if (thumbSizeRatio < 1f) { // Only show if the content is scrollable

            // Calculate the dynamic thumb height in pixels
            val minThumbHeightPx = with(density) { 24.dp.toPx() } // Set a minimum height
            val dynamicThumbHeightPx = (trackHeightPx.toFloat() * thumbSizeRatio).coerceAtLeast(minThumbHeightPx)

            // Calculate the vertical offset (Y-position) of the thumb
            // The thumb moves within the range (trackHeight - dynamicThumbHeight)
            val offsetY = ((trackHeightPx.toFloat() - dynamicThumbHeightPx) * scrollProgress).roundToInt()

            // Scrollbar Track container
            Box(
                    modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(vertical = 12.dp)
                            .width(6.dp)
                            .fillMaxHeight()
                            // Get the track's height once it is laid out
                            .onGloballyPositioned { layoutCoordinates ->
                                trackHeightPx = layoutCoordinates.size.height
                            }
                            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(3.dp))
                            .alpha(alpha)
            ) {
                // Scrollbar Thumb (The moving part)
                Spacer(
                        modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                // Use the calculated dynamic height for the thumb
                                .height(with(density) { dynamicThumbHeightPx.toDp() })
                                // Move the thumb vertically using the calculated offset
                                .offset { IntOffset(x = 0, y = offsetY) }
                                .background(Color.DarkGray, RoundedCornerShape(3.dp))
                )
            }
        }
    }
}
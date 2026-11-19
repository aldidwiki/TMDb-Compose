package com.aldiprahasta.tmdb.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

@Composable
fun SwipeBox(
        modifier: Modifier = Modifier,
        onDelete: () -> Unit,
        content: @Composable () -> Unit
) {
    val positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
    // Undo restores the row without restoring its previous swipe gesture.
    val swipeState = remember(LocalDensity.current) {
        SwipeToDismissBoxState(SwipeToDismissBoxValue.Settled, positionalThreshold)
    }
    val delete by rememberUpdatedState(onDelete)

    SwipeToDismissBox(
            modifier = modifier.animateContentSize(),
            state = swipeState,
            enableDismissFromStartToEnd = false,
            backgroundContent = {
                if (swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                    Box(
                            contentAlignment = Alignment.CenterEnd,
                            modifier = Modifier.fillMaxSize().background(Color.Red)
                    ) {
                        Icon(
                                modifier = Modifier.minimumInteractiveComponentSize(),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = Color.White
                        )
                    }
                }
            }
    ) {
        content()
    }

    LaunchedEffect(swipeState.settledValue) {
        if (swipeState.settledValue == SwipeToDismissBoxValue.EndToStart) {
            swipeState.snapTo(SwipeToDismissBoxValue.Settled)
            delete()
        }
    }
}

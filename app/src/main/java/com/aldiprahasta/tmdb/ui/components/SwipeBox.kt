package com.aldiprahasta.tmdb.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SwipeBox(
        modifier: Modifier = Modifier,
        onDelete: () -> Unit,
        content: @Composable () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
            modifier = modifier.animateContentSize(),
            state = swipeState,
            enableDismissFromStartToEnd = false,
            backgroundContent = {
                Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Red)
                ) {
                    Icon(
                            modifier = Modifier.minimumInteractiveComponentSize(),
                            imageVector = Icons.Outlined.Delete, contentDescription = null,
                            tint = Color.White
                    )
                }
            }
    ) {
        content()
    }

    when (swipeState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            onDelete()
        }

        SwipeToDismissBoxValue.StartToEnd -> {
        }

        SwipeToDismissBoxValue.Settled -> {
        }
    }
}
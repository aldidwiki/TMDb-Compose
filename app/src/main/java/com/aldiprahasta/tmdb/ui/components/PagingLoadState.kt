package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

internal fun <T : Any> LazyListScope.setupPagingLoadState(lazyPagingItems: LazyPagingItems<T>) {
    lazyPagingItems.apply {
        when {
            loadState.refresh is LoadState.Loading -> {
                item { LoadingScreen(modifier = Modifier.fillParentMaxSize()) }
            }

            loadState.refresh is LoadState.Error -> {
                item {
                    ErrorScreen(
                            modifier = Modifier.fillParentMaxSize()
                    )
                }
            }

            loadState.append is LoadState.Loading -> {
                item {
                    LoadingScreen(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                            indicatorSizeInDp = 30.dp
                    )
                }
            }

            loadState.append is LoadState.Error -> {
                val error = loadState.append as LoadState.Error
                item {
                    PagingErrorFooter(
                            errorMessage = error.error.localizedMessage,
                            onRetryClicked = { retry() }
                    )
                }
            }
        }
    }
}

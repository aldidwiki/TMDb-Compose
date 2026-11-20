package com.aldiprahasta.tmdb.ui.details.similar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.SimilarContent
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.DiscoverTopBar
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.UiState
import java.text.NumberFormat
import org.koin.androidx.compose.koinViewModel

@Composable
fun SimilarContentScreen(
        isTv: Boolean,
        contentTitle: String,
        genres: String,
        onBackPressed: () -> Unit,
        onItemClicked: (Int) -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: SimilarContentViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    DiscoverTheme {
        val title = stringResource(similarTitle(isTv))
        Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = { DiscoverTopBar(title, onBackPressed) }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                val total = (state as? UiState.Success)?.data?.totalResults
                SimilarContentHeader(contentTitle, genres, total)

                when (val feed = state) {
                    UiState.Loading -> LoadingScreen()
                    is UiState.Error -> DiscoverStatus(
                            message = stringResource(R.string.discover_load_error),
                            modifier = Modifier.fillMaxSize(),
                            onRetry = viewModel::retry
                    )
                    is UiState.Success -> {
                        val items = feed.data.items.collectAsLazyPagingItems()
                        SimilarContentGrid(items, onItemClicked)
                    }
                }
            }
        }
    }
}

@Composable
private fun SimilarContentHeader(contentTitle: String, genres: String, total: Int?) {
    if (contentTitle.isBlank() && genres.isBlank() && total == null) return

    Column(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                    text = if (contentTitle.isBlank()) "" else stringResource(R.string.detail_more_like, contentTitle),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
            )
            total?.let { count ->
                Text(
                        text = pluralStringResource(R.plurals.detail_title_count, count, NumberFormat.getIntegerInstance().format(count)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (genres.isNotBlank()) {
            Text(
                    text = genres.replace(", ", " · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SimilarContentGrid(items: LazyPagingItems<SimilarContent>, onItemClicked: (Int) -> Unit) {
    if (items.itemCount == 0) {
        when (items.loadState.refresh) {
            LoadState.Loading -> LoadingScreen()
            is LoadState.Error -> DiscoverStatus(stringResource(R.string.discover_load_error), Modifier.fillMaxSize(), items::retry)
            is LoadState.NotLoading -> DiscoverStatus(stringResource(R.string.detail_similar_empty), Modifier.fillMaxSize())
        }
        return
    }

    LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(count = items.itemCount) { index ->
            items[index]?.let { item ->
                SimilarContentCard(item, showRating = true, onClick = { onItemClicked(item.id) })
            }
        }
        when (items.loadState.append) {
            is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                DiscoverStatus(stringResource(R.string.discover_load_error), Modifier.fillMaxWidth(), items::retry)
            }
            LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                LoadingScreen(Modifier.fillMaxWidth().heightIn(min = 72.dp), indicatorSizeInDp = 28.dp)
            }
            is LoadState.NotLoading -> Unit
        }
    }
}

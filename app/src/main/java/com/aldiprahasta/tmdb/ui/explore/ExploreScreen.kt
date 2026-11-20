package com.aldiprahasta.tmdb.ui.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ExploreQuery
import com.aldiprahasta.tmdb.domain.model.ExploreTab
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.MediaType
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

internal val ExploreInset = 18.dp

private enum class ExploreDrawer { FILTER, SORT }

@Composable
fun ExploreScreen(onItemClicked: (Int, MediaType) -> Unit, modifier: Modifier = Modifier) {
    val viewModel: ExploreViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val results = viewModel.results.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var drawer by rememberSaveable { mutableStateOf<ExploreDrawer?>(null) }
    val onEvent: (ExploreEvent) -> Unit = { event ->
        viewModel.onEvent(event)
        if (event != ExploreEvent.RetryGenres) scope.launch { listState.scrollToItem(0) }
    }

    DiscoverTheme {
        Surface(
                modifier = modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
        ) {
            Column(
                    modifier = Modifier.fillMaxSize().windowInsetsPadding(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                    )
            ) {
                val title = exploreTitle(state.query)
                val failed = results.itemCount == 0 && results.loadState.refresh is LoadState.Error
                val loading = results.loadState.refresh is LoadState.Loading ||
                        (results.itemCount == 0 && !failed && !results.loadState.append.endOfPaginationReached)
                val empty = results.itemCount == 0 && !loading && !failed
                val headerTitle = when {
                    loading -> stringResource(state.tab.loadingTitle)
                    failed -> stringResource(state.tab.errorTitle)
                    empty -> stringResource(R.string.explore_no_results_heading)
                    else -> title
                }
                val headerStatus = when {
                    loading -> stringResource(R.string.explore_loading_subtitle)
                    failed -> stringResource(R.string.explore_error_subtitle)
                    empty -> stringResource(R.string.explore_empty_subtitle)
                    else -> null
                }
                ExploreHeader(
                        query = state.query,
                        title = headerTitle,
                        status = headerStatus,
                        onEvent = onEvent,
                        onFilter = { drawer = ExploreDrawer.FILTER },
                        onSort = { drawer = ExploreDrawer.SORT }
                )
                ExploreResults(
                        results = results,
                        state = state,
                        listState = listState,
                        title = if (empty) stringResource(R.string.explore_no_matching) else title,
                        loading = loading,
                        onReset = { onEvent(ExploreEvent.ResetFilters) },
                        onItemClicked = onItemClicked,
                        modifier = Modifier.weight(1f)
                )
            }
        }

        when (drawer) {
            ExploreDrawer.FILTER -> ExploreFilterDrawer(state, onDismiss = { drawer = null }, onEvent = onEvent)
            ExploreDrawer.SORT -> ExploreSortDrawer(state.query, onDismiss = { drawer = null }, onEvent = onEvent)
            null -> Unit
        }
    }
}

@Composable
private fun exploreTitle(query: ExploreQuery): String {
    val base = stringResource(if (query.tab == ExploreTab.ALL) R.string.explore_all else query.tab.label)
    val category = stringResource(query.tab.label)
    return when {
        query.filters.hasTitleFilters || query.filters.hasPeopleFilters -> category + " · " + stringResource(R.string.explore_filtered)
        query.sort != null -> category + " · " + stringResource(query.order.label)
        query.preset != null -> stringResource(query.preset.label)
        else -> base
    }
}

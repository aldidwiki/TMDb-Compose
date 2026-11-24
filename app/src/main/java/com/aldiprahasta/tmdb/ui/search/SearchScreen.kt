package com.aldiprahasta.tmdb.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
        onItemClicked: (contentId: Int, mediaType: String) -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: SearchViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DiscoverTheme {
        SearchContent(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onItemClicked = onItemClicked,
                modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchContent(
        uiState: SearchState,
        onEvent: (SearchEvent) -> Unit,
        onItemClicked: (Int, String) -> Unit,
        modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val activeFeed = uiState.feeds[uiState.category]
    val queryState = rememberTextFieldState(initialText = uiState.query)

    LaunchedEffect(queryState, onEvent) {
        snapshotFlow { queryState.text.toString() }.collect { query ->
            onEvent(SearchEvent.OnSearchQueryChanged(query))
        }
    }

    Scaffold(
            modifier = modifier.fillMaxSize().imePadding(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
            topBar = {
                TopAppBar(
                        title = {
                            Column {
                                Text(
                                        text = stringResource(R.string.dashboard_tab_search),
                                        style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                        text = stringResource(R.string.search_subtitle),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                        )
                )
            }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            SearchField(
                    state = queryState,
                    onSearch = { focusManager.clearFocus() },
                    modifier = Modifier.padding(start = 18.dp, top = 12.dp, end = 18.dp, bottom = 6.dp)
            )

            if (uiState.query.isNotBlank()) {
                SearchCategoryChips(
                        uiState = uiState,
                        onSelected = { category ->
                            focusManager.clearFocus()
                            onEvent(SearchEvent.OnCategorySelected(category))
                        }
                )
                Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = stringResource(R.string.search_results),
                            style = MaterialTheme.typography.titleSmall
                    )
                    if (activeFeed is UiState.Success) {
                        val count = activeFeed.data.totalResults
                        Text(
                                text = pluralStringResource(R.plurals.search_matches, count, formatSearchCount(count)),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                key(uiState.query.trim(), uiState.category) {
                    SearchResultList(
                            feed = activeFeed,
                            movieGenreNames = uiState.movieGenreNames,
                            onRetry = { onEvent(SearchEvent.Retry) },
                            onItemClicked = { result ->
                                focusManager.clearFocus()
                                onItemClicked(result.id, result.mediaType)
                            },
                            modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

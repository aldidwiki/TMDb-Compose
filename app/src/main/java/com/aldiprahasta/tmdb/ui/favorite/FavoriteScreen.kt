package com.aldiprahasta.tmdb.ui.favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.ui.components.DiscoverOptionsSheet
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteScreen(
        onItemClicked: (contentId: Int, mediaType: String) -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: FavoriteViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val removedMessage = stringResource(R.string.favorite_removed)
    val undoLabel = stringResource(R.string.favorite_undo)
    val writeError = stringResource(R.string.favorite_write_error)

    LaunchedEffect(viewModel, removedMessage, undoLabel, writeError) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FavoriteEffect.Removed -> {
                    val result = snackbarHostState.showSnackbar(
                            message = removedMessage,
                            actionLabel = undoLabel,
                            duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(FavoriteEvent.UndoRemove(effect.favorite))
                    }
                }

                FavoriteEffect.WriteFailed -> snackbarHostState.showSnackbar(writeError)
            }
        }
    }

    DiscoverSystemBars()
    DiscoverTheme {
        FavoriteContent(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onEvent = viewModel::onEvent,
                onItemClicked = onItemClicked,
                modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteContent(
        uiState: FavoriteState,
        snackbarHostState: SnackbarHostState,
        onEvent: (FavoriteEvent) -> Unit,
        onItemClicked: (Int, String) -> Unit,
        modifier: Modifier = Modifier
) {
    var showSortSheet by rememberSaveable { mutableStateOf(false) }
    val favorites = uiState.favorites

    Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                        title = {
                            Column {
                                Text(
                                        text = stringResource(R.string.dashboard_tab_favorites),
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                                if (favorites is UiState.Success) {
                                    val count = favorites.data.size
                                    Text(
                                            text = pluralStringResource(R.plurals.favorite_count, count, count),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        actions = {
                            FavoriteSortButton(
                                    order = uiState.sortOrder,
                                    onClick = { showSortSheet = true }
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                        )
                )
            }
    ) { innerPadding ->
        val contentModifier = Modifier.fillMaxSize().padding(innerPadding)

        when (favorites) {
            UiState.Loading -> LoadingScreen(contentModifier)
            is UiState.Error -> ErrorScreen(
                    modifier = contentModifier,
                    errorMessage = stringResource(R.string.favorite_load_error)
            )

            is UiState.Success -> {
                key(uiState.sortOrder) {
                    FavoriteList(
                            favorites = favorites.data,
                            onRemove = { onEvent(FavoriteEvent.Remove(it)) },
                            onItemClicked = onItemClicked,
                            modifier = contentModifier,
                    )
                }
            }
        }
    }

    if (showSortSheet) {
        DiscoverOptionsSheet(
                title = stringResource(R.string.favorite_sort),
                description = stringResource(R.string.favorite_sort_description),
                applyLabel = stringResource(R.string.genre_apply_sort),
                options = FavoriteSortOrder.entries,
                appliedOption = uiState.sortOrder,
                optionLabel = { stringResource(it.label) },
                onDismiss = { showSortSheet = false },
                onApply = {
                    showSortSheet = false
                    onEvent(FavoriteEvent.ApplySort(it))
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteContentPreview() {
    DiscoverTheme {
        FavoriteContent(
                uiState = FavoriteState(favorites = UiState.Success(listOf(favoritePreviewItem))),
                snackbarHostState = remember { SnackbarHostState() },
                onEvent = {},
                onItemClicked = { _, _ -> }
        )
    }
}

package com.aldiprahasta.tmdb.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.DiscoverTopBar
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.details.similar.SimilarContentRail
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ContentDetailScreen(
        contentId: Int,
        mediaType: MediaType,
        onBackPressed: () -> Unit,
        onCastClicked: (Int) -> Unit,
        onFullCastClicked: (String) -> Unit,
        onSimilarClicked: (Int) -> Unit,
        onAllSimilarClicked: (title: String, genres: String) -> Unit,
        onAllEpisodesClicked: (Int) -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: ContentDetailViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val favoriteError = stringResource(R.string.favorite_write_error)
    val isTv = mediaType == MediaType.TV_TYPE

    LaunchedEffect(contentId, mediaType) {
        viewModel.onEvent(ContentDetailEvent.Initialize(contentId, mediaType))
    }
    LaunchedEffect(viewModel, favoriteError) {
        viewModel.favoriteErrors.collect { snackbarHostState.showSnackbar(favoriteError) }
    }

    DiscoverSystemBars()
    DiscoverTheme {
        Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    DiscoverTopBar(
                            title = stringResource(if (isTv) R.string.detail_tv_title else R.string.detail_movie_title),
                            onBackPressed = onBackPressed
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            val contentModifier = Modifier.fillMaxSize().padding(innerPadding)
            when (val detailState = state.detail) {
                UiState.Loading -> LoadingScreen(contentModifier)
                is UiState.Error -> DiscoverStatus(
                        message = stringResource(R.string.discover_load_error),
                        modifier = contentModifier,
                        onRetry = { viewModel.onEvent(ContentDetailEvent.RetryDetail) }
                )
                is UiState.Success -> {
                    val detail = detailState.data
                    Column(
                            modifier = contentModifier.verticalScroll(rememberScrollState()).padding(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        ContentDetailHero(detail, isTv)
                        ContentDetailOverview(
                                detail = detail,
                                isTv = isTv,
                                favorite = state.isFavorite,
                                favoriteEnabled = state.isFavoriteAvailable && !state.isSavingFavorite,
                                onFavoriteChanged = { viewModel.onEvent(ContentDetailEvent.OnFavoriteClicked(it)) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        )
                        if (isTv) {
                            ContentSeasons(
                                    seasons = detail.seasons.orEmpty(),
                                    selectedSeason = state.selectedSeason,
                                    seasonDetails = state.selectedSeason?.let(state.seasons::get),
                                    onSeasonSelected = { viewModel.onEvent(ContentDetailEvent.SelectSeason(it)) },
                                    onSeeAll = onAllEpisodesClicked,
                                    onRetry = { viewModel.onEvent(ContentDetailEvent.RetrySeason) }
                            )
                        }
                        ContentTopCast(detail.casts, onCastClicked, onSeeAll = { onFullCastClicked(detail.title) })
                        ContentDetailInfo(detail, isTv, Modifier.fillMaxWidth().padding(horizontal = 20.dp))
                        SimilarContentRail(
                                state = state.similar,
                                isTv = isTv,
                                onItemClicked = onSimilarClicked,
                                onSeeAll = { onAllSimilarClicked(detail.title, detail.genres) },
                                onRetry = { viewModel.onEvent(ContentDetailEvent.RetrySimilar) }
                        )
                    }
                }
            }
        }
    }
}

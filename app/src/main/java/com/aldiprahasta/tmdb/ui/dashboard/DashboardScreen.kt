package com.aldiprahasta.tmdb.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.domain.model.VideoDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverBottomSheet
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.openBrowser
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private val DashboardContentInset = 18.dp

@Composable
fun DashboardScreen(
        onMovieClicked: (movieId: Int) -> Unit,
        onGenreClicked: (GenreDomainModel) -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: DashboardViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DiscoverSystemBars()
    DiscoverTheme {
        DashboardContent(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onMovieClicked = onMovieClicked,
                onGenreClicked = onGenreClicked,
                modifier = modifier
        )
    }
}

@Composable
private fun DashboardContent(
        uiState: DashboardState,
        onEvent: (DashboardEvent) -> Unit,
        onMovieClicked: (movieId: Int) -> Unit,
        onGenreClicked: (GenreDomainModel) -> Unit,
        modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val genreNames = (uiState.genres as? UiState.Success)?.data.orEmpty().associate { it.id to it.name }

    Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
        )) {
            LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 18.dp, bottom = 24.dp)
            ) {
                item(key = "brand") {
                    Text(
                            text = stringResource(R.string.app_name),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(
                                    start = DashboardContentInset,
                                    end = DashboardContentInset,
                                    bottom = 20.dp
                            )
                    )
                }
                item(key = "trending") {
                    TrendingSection(
                            movies = uiState.trendingMovies,
                            genreNames = genreNames,
                            isTrailerLoading = uiState.trailers == UiState.Loading,
                            onRetry = { onEvent(DashboardEvent.RetryTrending) },
                            onMovieClicked = onMovieClicked,
                            onWatchTrailer = { onEvent(DashboardEvent.OnWatchTrailerClicked(it)) },
                            contentInset = DashboardContentInset
                    )
                }
                item(key = "nowPlaying") {
                    NowPlayingSection(
                            movies = uiState.nowPlayingMovies,
                            onRetry = { onEvent(DashboardEvent.RetryNowPlaying) },
                            onMovieClicked = onMovieClicked,
                            contentInset = DashboardContentInset,
                            modifier = Modifier.padding(top = 20.dp)
                    )
                }
                dashboardGenres(
                        genres = uiState.genres,
                        onRetry = { onEvent(DashboardEvent.RetryGenres) },
                        onGenreClicked = onGenreClicked,
                        contentInset = DashboardContentInset
                )
            }

            SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
        }
    }

    DashboardTrailers(
            trailerState = uiState.trailers,
            onDismiss = { onEvent(DashboardEvent.DismissTrailers) },
            snackbarHostState = snackbarHostState
    )
}

@Composable
private fun DashboardTrailers(
        trailerState: UiState<List<VideoDomainModel>>?,
        onDismiss: () -> Unit,
        snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val snackbarScope = rememberCoroutineScope()
    val unavailable = stringResource(R.string.dashboard_trailer_unavailable)
    val error = stringResource(R.string.dashboard_trailer_error)
    val trailers = (trailerState as? UiState.Success)?.data

    LaunchedEffect(trailerState) {
        when {
            trailerState is UiState.Error -> {
                onDismiss()
                snackbarScope.launch { snackbarHostState.showSnackbar(error) }
            }

            trailers?.isEmpty() == true -> {
                onDismiss()
                snackbarScope.launch { snackbarHostState.showSnackbar(unavailable) }
            }

            trailers?.size == 1 -> {
                onDismiss()
                context.openBrowser(Constant.YOUTUBE_BASE_URL + trailers.single().key)
            }
        }
    }

    if (trailers != null && trailers.size > 1) {
        DiscoverBottomSheet(
                title = stringResource(R.string.dashboard_trailers),
                description = stringResource(R.string.dashboard_choose_trailer),
                onDismiss = onDismiss
        ) {
            trailers.forEach { trailer ->
                TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onDismiss()
                            context.openBrowser(Constant.YOUTUBE_BASE_URL + trailer.key)
                        }
                ) {
                    Text(
                            text = trailer.name.ifBlank { stringResource(R.string.dashboard_watch_trailer) },
                            style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

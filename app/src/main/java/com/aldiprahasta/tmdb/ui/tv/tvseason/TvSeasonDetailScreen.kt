package com.aldiprahasta.tmdb.ui.tv.tvseason

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.DiscoverTopBar
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.ui.tv.TvViewModel
import com.aldiprahasta.tmdb.utils.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun TvSeasonDetailScreen(
        tvId: Int,
        tvSeasonNumber: Int,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: TvViewModel = koinViewModel()
    val season by viewModel.tvSeasonDetail.collectAsStateWithLifecycle()

    LaunchedEffect(tvId, tvSeasonNumber) { viewModel.setTvSeasonDetailParam(tvId, tvSeasonNumber) }

    DiscoverSystemBars()
    DiscoverTheme {
        Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = { DiscoverTopBar(stringResource(R.string.detail_episodes), onBackPressed) }
        ) { innerPadding ->
            val content = Modifier.fillMaxSize().padding(innerPadding)
            when (val state = season) {
                UiState.Loading -> LoadingScreen(content)
                is UiState.Error -> DiscoverStatus(
                        message = stringResource(R.string.discover_load_error),
                        modifier = content,
                        onRetry = viewModel::retrySeason
                )
                is UiState.Success -> SeasonEpisodes(
                        season = state.data,
                        modifier = content
                )
            }
        }
    }
}

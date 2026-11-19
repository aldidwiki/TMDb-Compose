package com.aldiprahasta.tmdb.ui.tv.tvseason

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.DiscoverTopBar
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.ui.tv.TvEpisodeItem
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
                is UiState.Success -> {
                    val episodes = state.data.tvEpisodeList
                    LazyColumn(
                            modifier = content,
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Column(Modifier.fillMaxWidth().padding(bottom = 6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(seasonLabel(state.data.tvSeasonDomainModel), style = MaterialTheme.typography.headlineSmall)
                                Text(
                                        text = seasonMetadata(state.data.tvSeasonDomainModel),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (episodes.isEmpty()) {
                            item {
                                DiscoverStatus(stringResource(R.string.detail_episodes_empty), Modifier.fillMaxWidth())
                            }
                        }
                        items(episodes, key = { it.id }) { episode ->
                            TvEpisodeItem(episode, showDetails = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun seasonLabel(season: TvSeasonDomainModel): String = season.seasonName.ifBlank {
    if (season.seasonNumber == 0) stringResource(R.string.detail_specials)
    else stringResource(R.string.detail_season_number, season.seasonNumber)
}

@Composable
internal fun seasonMetadata(season: TvSeasonDomainModel): String {
    val count = season.totalEpisodes
    val episodes = pluralStringResource(R.plurals.detail_episode_count, count, count)
    val year = season.airDateIso?.take(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) }
    return listOfNotNull(episodes, year).joinToString(" · ")
}

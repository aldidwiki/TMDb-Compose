package com.aldiprahasta.tmdb.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.TvEpisodeDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import com.aldiprahasta.tmdb.ui.tv.TvEpisodeItem
import com.aldiprahasta.tmdb.ui.tv.tvseason.seasonLabel
import com.aldiprahasta.tmdb.ui.tv.tvseason.seasonMetadata
import com.aldiprahasta.tmdb.utils.UiState

@Composable
internal fun ContentSeasons(
        seasons: List<TvSeasonDomainModel>,
        selectedSeason: Int?,
        seasonDetails: UiState<TvSeasonDetailDomainModel>?,
        onSeasonSelected: (Int) -> Unit,
        onSeeAll: (Int) -> Unit,
        onRetry: () -> Unit
) {
    if (seasons.isEmpty()) return

    val sortedSeasons = remember(seasons) { seasons.distinctBy { it.seasonNumber }.sortedBy { it.seasonNumber } }
    val selection = sortedSeasons.firstOrNull { it.seasonNumber == selectedSeason }
    val episodes = (seasonDetails as? UiState.Success)?.data?.tvEpisodeList.orEmpty()
    val seasonScroll = rememberLazyListState()
    LaunchedEffect(sortedSeasons, selectedSeason) {
        val index = sortedSeasons.indexOfFirst { it.seasonNumber == selectedSeason }
        val visibleItems = seasonScroll.layoutInfo.visibleItemsInfo
        if (index >= 0 && visibleItems.none { it.index == index }) seasonScroll.scrollToItem(index)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DetailSectionHeader(R.string.detail_seasons, Modifier.padding(horizontal = 20.dp))
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                        modifier = Modifier.weight(1f),
                        state = seasonScroll,
                        contentPadding = PaddingValues(start = 20.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedSeasons, key = { it.seasonNumber }) { season ->
                        FilterChip(
                                selected = season.seasonNumber == selectedSeason,
                                onClick = { onSeasonSelected(season.seasonNumber) },
                                label = { Text(seasonLabel(season), style = MaterialTheme.typography.labelMedium) },
                                shape = RoundedCornerShape(18.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                        )
                    }
                }
                selection?.let { season ->
                    Text(
                            text = seasonMetadata(season),
                            modifier = Modifier.padding(start = 10.dp, end = 20.dp).widthIn(max = 130.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End
                    )
                }
            }
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DetailSectionHeader(
                    title = R.string.detail_episodes,
                    onSeeAll = selection?.seasonNumber?.takeIf { episodes.isNotEmpty() }?.let { number ->
                        { onSeeAll(number) }
                    }
            )
            when (seasonDetails) {
                null, UiState.Loading -> LoadingScreen(Modifier.fillMaxWidth().heightIn(min = 96.dp), indicatorSizeInDp = 28.dp)
                is UiState.Error -> DiscoverStatus(stringResource(R.string.discover_load_error), Modifier.fillMaxWidth(), onRetry)
                is UiState.Success -> {
                    if (episodes.isEmpty()) {
                        DiscoverStatus(stringResource(R.string.detail_episodes_empty), Modifier.fillMaxWidth())
                    } else {
                        episodes.take(3).forEach { episode -> TvEpisodeItem(episode) }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 390)
@Composable
private fun ContentSeasonsPreview() {
    DiscoverPreview {
        val season = TvSeasonDomainModel(1, "Season 1", null, "2023", 8.3, 9, 1, "2023-01-15")
        val episode = TvEpisodeDomainModel(
                id = 1,
                name = "When You’re Lost in the Darkness",
                stillPath = null,
                runtime = 81,
                overview = "Joel and Ellie begin their journey.",
                voteAverage = 8.6,
                episodeNumber = 1,
                airDate = "January 15, 2023"
        )
        ContentSeasons(
                seasons = listOf(season, season.copy(seasonId = 2, seasonName = "Season 2", seasonNumber = 2)),
                selectedSeason = 1,
                seasonDetails = UiState.Success(TvSeasonDetailDomainModel(season, listOf(episode))),
                onSeasonSelected = {},
                onSeeAll = {},
                onRetry = {}
        )
    }
}

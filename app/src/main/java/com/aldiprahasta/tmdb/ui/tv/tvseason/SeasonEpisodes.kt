package com.aldiprahasta.tmdb.ui.tv.tvseason

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.TvEpisodeDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import com.aldiprahasta.tmdb.ui.tv.TvEpisodeItem

@Composable
internal fun SeasonEpisodes(season: TvSeasonDetailDomainModel, modifier: Modifier = Modifier) {
    val episodes = season.tvEpisodeList
    LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column(Modifier.fillMaxWidth().padding(bottom = 6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(seasonLabel(season.tvSeasonDomainModel), style = MaterialTheme.typography.headlineSmall)
                Text(
                        text = seasonMetadata(season.tvSeasonDomainModel),
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

@Preview(widthDp = 390, heightDp = 340)
@Composable
private fun SeasonEpisodesPreview() {
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
        SeasonEpisodes(TvSeasonDetailDomainModel(season, listOf(episode)))
    }
}

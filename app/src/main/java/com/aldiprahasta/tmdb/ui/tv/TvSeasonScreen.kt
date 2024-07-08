package com.aldiprahasta.tmdb.ui.tv

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel

@Composable
fun TvSeasonScreen(
        tvTitle: String,
        tvSeasonList: List<TvSeasonDomainModel>,
        modifier: Modifier = Modifier
) {
    LazyColumn(
            modifier = modifier
                    .fillMaxSize()
                    .padding(20.dp)
    ) {
        itemsIndexed(tvSeasonList) { index, tvSeason ->
            TvSeasonItem(
                    tvTitle = tvTitle,
                    tvSeasonDomainModel = tvSeason
            )

            if (index < tvSeasonList.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TvSeasonScreenPreview(modifier: Modifier = Modifier) {
    TvSeasonScreen(
            tvTitle = "House of Dragon",
            tvSeasonList = listOf(
                    TvSeasonDomainModel(
                            seasonId = 2237,
                            seasonName = "Season 1",
                            seasonPosterPath = null,
                            seasonAirDate = "August 10, 2022",
                            seasonVoteAverage = 8.0,
                            totalEpisodes = 53,
                            seasonNumber = 1
                    ),
                    TvSeasonDomainModel(
                            seasonId = 2237,
                            seasonName = "Season 1",
                            seasonPosterPath = null,
                            seasonAirDate = "August 10, 2022",
                            seasonVoteAverage = 8.0,
                            totalEpisodes = 53,
                            seasonNumber = 1
                    ),
                    TvSeasonDomainModel(
                            seasonId = 2237,
                            seasonName = "Season 1",
                            seasonPosterPath = null,
                            seasonAirDate = "August 10, 2022",
                            seasonVoteAverage = 8.0,
                            totalEpisodes = 53,
                            seasonNumber = 1
                    ),
                    TvSeasonDomainModel(
                            seasonId = 2237,
                            seasonName = "Season 1",
                            seasonPosterPath = null,
                            seasonAirDate = "August 10, 2022",
                            seasonVoteAverage = 8.0,
                            totalEpisodes = 53,
                            seasonNumber = 1
                    ),
            )
    )
}
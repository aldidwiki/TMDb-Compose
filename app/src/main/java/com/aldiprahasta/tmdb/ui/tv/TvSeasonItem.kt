package com.aldiprahasta.tmdb.ui.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageLoader
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.VoteAverageUi

@Composable
fun TvSeasonItem(
        tvTitle: String,
        tvSeasonDomainModel: TvSeasonDomainModel,
        modifier: Modifier = Modifier
) {
    var seasonOverview = "Season ${tvSeasonDomainModel.seasonNumber} of $tvTitle premiered on ${tvSeasonDomainModel.seasonAirDate}."

    Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
    ) {
        ImageLoader(
                imagePath = tvSeasonDomainModel.seasonPosterPath,
                imageType = ImageType.POSTER,
                modifier = Modifier
                        .width(100.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(6.dp))
        )

        Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Text(
                    text = tvSeasonDomainModel.seasonName,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
            )

            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (tvSeasonDomainModel.seasonVoteAverage != 0.0) {
                    VoteAverageUi(voteAverage = tvSeasonDomainModel.seasonVoteAverage)
                }

                if (tvSeasonDomainModel.seasonAirDate.isNotEmpty()) {
                    Text(
                            text = tvSeasonDomainModel.seasonAirDate.takeLast(4),
                            style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                            text = "\u2022",
                            fontSize = 14.sp
                    )
                } else {
                    seasonOverview = "We don't have an overview translated in English. Help us expand our database by adding one."
                }

                Text(
                        text = "${tvSeasonDomainModel.totalEpisodes} Episodes",
                        style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.size(8.dp))
            Text(
                    text = seasonOverview,
                    style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TvSeasonItemPreview(modifier: Modifier = Modifier) {
    TvSeasonItem(
            tvTitle = "House of Dragon",
            tvSeasonDomainModel = TvSeasonDomainModel(
                    seasonId = 2237,
                    seasonName = "Season 1",
                    seasonPosterPath = null,
                    seasonAirDate = "August 10, 2022",
                    seasonVoteAverage = 2.3,
                    totalEpisodes = 53,
                    seasonNumber = 1
            ))
}
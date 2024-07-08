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

@Composable
fun TvSeasonItem(
        tvTitle: String,
        tvSeasonDomainModel: TvSeasonDomainModel,
        modifier: Modifier = Modifier
) {
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
            )

            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                        text = tvSeasonDomainModel.seasonAirDate.take(4),
                        style = MaterialTheme.typography.titleSmall
                )
                Text(
                        text = "\u2022",
                        fontSize = 18.sp
                )
                Text(
                        text = "${tvSeasonDomainModel.totalEpisodes} Episodes",
                        style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.size(8.dp))
            Text(
                    text = "Season ${tvSeasonDomainModel.seasonNumber} of $tvTitle premiered on ${tvSeasonDomainModel.seasonAirDate}.",
                    style = MaterialTheme.typography.bodyMedium
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
                    seasonAirDate = "2022-04-12",
                    seasonVoteAverage = 2.3,
                    totalEpisodes = 53,
                    seasonNumber = 1
            ))
}
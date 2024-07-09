package com.aldiprahasta.tmdb.ui.tv

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.domain.model.TvEpisodeDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageLoader
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.VoteAverageUi
import com.aldiprahasta.tmdb.utils.runtimeFormat

@Composable
fun TvEpisodeItem(
        tvEpisodeDomainModel: TvEpisodeDomainModel,
        modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        ImageLoader(
                imagePath = tvEpisodeDomainModel.stillPath,
                imageType = ImageType.STILL,
                modifier = Modifier.height(170.dp)
        )

        Column(modifier = Modifier
                .padding(10.dp)
                .padding(bottom = 10.dp)
        ) {
            Text(
                    text = "${tvEpisodeDomainModel.episodeNumber} ${tvEpisodeDomainModel.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.size(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                VoteAverageUi(voteAverage = tvEpisodeDomainModel.voteAverage)
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                        text = tvEpisodeDomainModel.airDate,
                        style = MaterialTheme.typography.bodySmall
                )
                Text(
                        text = "\u2022",
                        modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                        text = tvEpisodeDomainModel.runtime.runtimeFormat(),
                        style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.size(10.dp))
            Text(
                    text = tvEpisodeDomainModel.overview,
                    style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TvEpisodeItemPreview() {
    TvEpisodeItem(tvEpisodeDomainModel = TvEpisodeDomainModel(
            id = 4755,
            name = "The Heirs of the Dragon",
            stillPath = null,
            runtime = 66,
            overview = "Viserys hosts a tournament to celebrate the birth of his second child. Rhaenyra welcomes her uncle Daemon back to the Red Keep.",
            voteAverage = 7.9,
            episodeNumber = 1,
            airDate = "August 21, 2022"
    ))
}
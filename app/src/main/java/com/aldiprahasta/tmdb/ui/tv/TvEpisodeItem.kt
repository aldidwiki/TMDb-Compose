package com.aldiprahasta.tmdb.ui.tv

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.TvEpisodeDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.utils.formatMovieRating

@Composable
internal fun TvEpisodeItem(
        episode: TvEpisodeDomainModel,
        modifier: Modifier = Modifier,
        showDetails: Boolean = false
) {
    Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                            Modifier.widthIn(min = 44.dp).heightIn(min = 40.dp).padding(horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                    ) {
                        Text(
                                stringResource(R.string.detail_episode_number, episode.episodeNumber),
                                style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                            text = episode.name.ifBlank {
                                stringResource(R.string.detail_episode_untitled, episode.episodeNumber)
                            },
                            style = MaterialTheme.typography.labelLarge
                    )
                    val runtime = if (showDetails && episode.runtime > 0) {
                        stringResource(R.string.detail_runtime_minutes, episode.runtime)
                    } else null
                    val rating = if (showDetails && episode.voteAverage > 0) {
                        stringResource(R.string.discover_rating_badge, formatMovieRating(episode.voteAverage))
                    } else null
                    val metadata = listOfNotNull(episode.airDate.takeIf(String::isNotBlank), runtime, rating)
                    if (metadata.isNotEmpty()) {
                        Text(
                                text = metadata.joinToString(" · "),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (showDetails) {
                if (!episode.stillPath.isNullOrBlank()) {
                    MovieArtwork(
                            imagePath = episode.stillPath,
                            imageType = ImageType.STILL,
                            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(8.dp))
                    )
                }
                if (episode.overview.isNotBlank()) {
                    Text(
                            text = episode.overview,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

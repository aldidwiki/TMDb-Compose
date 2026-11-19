package com.aldiprahasta.tmdb.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.ui.components.AnimatedFavoriteButton
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.ui.components.TrailerSheet
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.formatMovieRating
import com.aldiprahasta.tmdb.utils.openBrowser
import com.aldiprahasta.tmdb.utils.youtubeTrailers
import java.text.NumberFormat

@Composable
internal fun ContentDetailOverview(
        detail: ContentDetailDomainModel,
        isTv: Boolean,
        favorite: Boolean,
        favoriteEnabled: Boolean,
        onFavoriteChanged: (Boolean) -> Unit,
        modifier: Modifier = Modifier
) {
    val trailers = remember(detail.videos) { detail.videos.youtubeTrailers() }
    var showTrailers by rememberSaveable { mutableStateOf(false) }
    if (showTrailers && trailers.isNotEmpty()) {
        val context = LocalContext.current
        TrailerSheet(
                trailers = trailers,
                onDismiss = { showTrailers = false },
                onTrailerClicked = { context.openBrowser(Constant.YOUTUBE_BASE_URL + it.key) }
        )
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MovieArtwork(
                    imagePath = detail.posterPath,
                    imageType = ImageType.POSTER,
                    modifier = Modifier.size(width = 96.dp, height = 144.dp)
                            .clip(RoundedCornerShape(12.dp))
            )
            Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (detail.voteAverage > 0 && detail.voteCount != 0) {
                    DetailRatingBadge(detail.voteAverage)
                }
                if (!isTv) {
                    detail.voteCount?.takeIf { it > 0 }?.let { count ->
                        Text(
                                text = pluralStringResource(
                                        R.plurals.detail_vote_count, count, NumberFormat.getIntegerInstance().format(count)
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (detail.overview.isNotBlank()) {
                    Text(
                            text = stringResource(R.string.detail_overview),
                            style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                            text = detail.overview,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                    onClick = { showTrailers = true },
                    enabled = trailers.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Text(stringResource(R.string.watch_trailer), style = MaterialTheme.typography.labelLarge)
            }
            AnimatedFavoriteButton(
                    checked = favorite,
                    enabled = favoriteEnabled,
                    onCheckedChange = onFavoriteChanged,
                    modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
internal fun DetailRatingBadge(rating: Double, modifier: Modifier = Modifier) {
    Text(
            text = stringResource(R.string.discover_rating_badge, formatMovieRating(rating)),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            modifier = modifier.background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.92f), RoundedCornerShape(7.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}


@Preview(widthDp = 390)
@Composable
private fun ContentDetailOverviewPreview() {
    DiscoverPreview {
        ContentDetailOverview(
                detail = detailPreviewSample,
                isTv = false,
                favorite = true,
                favoriteEnabled = true,
                onFavoriteChanged = {}
        )
    }
}

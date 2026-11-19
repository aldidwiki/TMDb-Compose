package com.aldiprahasta.tmdb.ui.details.similar

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.SimilarContent
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.ui.details.DetailRatingBadge

@StringRes
internal fun similarTitle(isTv: Boolean): Int =
        if (isTv) R.string.detail_similar_shows else R.string.detail_similar_movies

@Composable
internal fun SimilarContentCard(
        item: SimilarContent,
        showRating: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier.clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(R.string.genre_see_details),
                    onClick = onClick
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(2f / 3f).clip(RoundedCornerShape(12.dp))) {
            MovieArtwork(item.posterPath, ImageType.POSTER, Modifier.matchParentSize())
            if (showRating) {
                item.voteAverage?.let { DetailRatingBadge(it, Modifier.align(Alignment.TopEnd).padding(10.dp)) }
            }
        }
        Text(item.title, style = MaterialTheme.typography.labelLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
        item.releaseYear?.let { year ->
            Text(year, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.ui.theme.DiscoverRating
import com.aldiprahasta.tmdb.utils.formatMovieRating

@Composable
internal fun DiscoverMediaCard(
        title: String,
        subtitle: String?,
        posterPath: String?,
        voteAverage: Double?,
        description: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        trailingContent: (@Composable () -> Unit)? = null
) {
    Surface(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = modifier.fillMaxWidth()
    ) {
        Box {
            Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MovieArtwork(
                        imagePath = posterPath,
                        imageType = ImageType.POSTER,
                        modifier = Modifier
                                .width(86.dp)
                                .height(134.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(end = if (trailingContent != null) 38.dp else 0.dp)
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    voteAverage?.let { rating ->
                        Text(
                                text = stringResource(R.string.discover_rating_badge, formatMovieRating(rating)),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = DiscoverRating
                        )
                    }
                    if (description.isNotBlank()) {
                        Text(
                                text = description,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                            onClick = onClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(stringResource(R.string.genre_see_details), Modifier.fillMaxWidth())
                    }
                }
            }
            if (trailingContent != null) {
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    trailingContent()
                }
            }
        }
    }
}

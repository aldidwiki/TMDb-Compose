package com.aldiprahasta.tmdb.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import com.aldiprahasta.tmdb.utils.UiState

@Composable
internal fun NowPlayingSection(
        movies: UiState<List<MovieDomainModel>>,
        onRetry: () -> Unit,
        onMovieClicked: (Int) -> Unit,
        contentInset: Dp,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DashboardSectionTitle(stringResource(R.string.dashboard_now_playing), contentInset)

        when (movies) {
            UiState.Loading -> DashboardSkeleton(150.dp, contentInset)
            is UiState.Error -> DashboardSectionMessage(contentInset, onRetry)
            is UiState.Success -> if (movies.data.isEmpty()) {
                DashboardSectionMessage(contentInset)
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val space = 14.dp
                    val posterWidth = ((maxWidth - contentInset * 2 - space * 2) / 3).coerceIn(80.dp, 146.dp)

                    LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = contentInset),
                            horizontalArrangement = Arrangement.spacedBy(space)
                    ) {
                        items(items = movies.data, key = { it.movieId }) { movie ->
                            NowPlayingCard(
                                    movie = movie,
                                    posterWidth = posterWidth,
                                    onClick = { onMovieClicked(movie.movieId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NowPlayingCard(movie: MovieDomainModel, posterWidth: Dp, onClick: () -> Unit) {
    val scrim = MaterialTheme.colorScheme.scrim

    Box(
            modifier = Modifier
                    .width(posterWidth)
                    .clip(RoundedCornerShape(12.dp))
                    .background(scrim)
                    .clickable(role = Role.Button, onClick = onClick)
    ) {
        MovieArtwork(
                imagePath = movie.posterPath,
                imageType = ImageType.POSTER,
                modifier = Modifier.matchParentSize()
        )
        Box(
                modifier = Modifier
                        .matchParentSize()
                        .background(Brush.verticalGradient(
                                0.45f to Color.Transparent,
                                0.73f to scrim.copy(alpha = 0.64f),
                                1f to scrim.copy(alpha = 0.9f)
                        ))
        )
        Column {
            Spacer(Modifier.height(posterWidth))
            Text(
                    text = movie.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Preview(widthDp = 390)
@Composable
private fun NowPlayingSectionPreview() {
    DiscoverPreview {
        val movie = MovieDomainModel(
                movieId = 1,
                title = "Dune: Part Two",
                posterPath = null,
                releaseDate = "March 1, 2024",
                releaseYear = "2024",
                voteAverage = 8.8
        )
        NowPlayingSection(
                movies = UiState.Success(listOf(movie, movie.copy(movieId = 2, title = "The Wild Robot"))),
                onRetry = {},
                onMovieClicked = {},
                contentInset = 18.dp
        )
    }
}

@Preview(widthDp = 124)
@Composable
private fun NowPlayingCardPreview() {
    DiscoverPreview {
        NowPlayingCard(movie = MovieDomainModel(
                movieId = 1,
                title = "Dune: Part Two",
                posterPath = null,
                releaseDate = "March 1, 2024",
                releaseYear = "2024",
                voteAverage = 8.8
        ), posterWidth = 110.dp, onClick = {})
    }
}

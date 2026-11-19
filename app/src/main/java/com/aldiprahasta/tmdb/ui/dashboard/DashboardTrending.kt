package com.aldiprahasta.tmdb.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.aldiprahasta.tmdb.ui.components.discoverHeroGradient
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.formatMovieRating

@Composable
internal fun TrendingSection(
        movies: UiState<List<MovieDomainModel>>,
        genreNames: Map<Int, String>,
        isTrailerLoading: Boolean,
        onRetry: () -> Unit,
        onMovieClicked: (Int) -> Unit,
        onWatchTrailer: (Int) -> Unit,
        contentInset: Dp
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DashboardSectionTitle(stringResource(R.string.dashboard_trending), contentInset)

        when (movies) {
            UiState.Loading -> DashboardSkeleton(250.dp, contentInset)
            is UiState.Error -> DashboardSectionMessage(contentInset, onRetry)
            is UiState.Success -> if (movies.data.isEmpty()) {
                DashboardSectionMessage(contentInset)
            } else {
                val pagerState = rememberPagerState(pageCount = { movies.data.size })

                HorizontalPager(
                        state = pagerState,
                        key = { index -> movies.data[index].movieId },
                        contentPadding = PaddingValues(horizontal = contentInset),
                        pageSpacing = contentInset,
                        modifier = Modifier.fillMaxWidth()
                ) { index ->
                    val movie = movies.data[index]
                    TrendingMovieCard(
                            movie = movie,
                            genreNames = movie.genreIds.mapNotNull(genreNames::get).take(2),
                            isTrailerLoading = isTrailerLoading,
                            onClick = { onMovieClicked(movie.movieId) },
                            onTrailerClick = { onWatchTrailer(movie.movieId) }
                    )
                }

                Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(movies.data.size) { index ->
                        val selected = pagerState.currentPage == index
                        Box(
                                modifier = Modifier
                                        .size(width = if (selected) 18.dp else 6.dp, height = 6.dp)
                                        .background(
                                                color = if (selected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.outline,
                                                shape = RoundedCornerShape(3.dp)
                                        )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendingMovieCard(
        movie: MovieDomainModel,
        genreNames: List<String>,
        isTrailerLoading: Boolean,
        onClick: () -> Unit,
        onTrailerClick: () -> Unit
) {
    val scrim = MaterialTheme.colorScheme.scrim
    val metadata = listOfNotNull(
            movie.releaseYear,
            genreNames.joinToString(" / ").ifBlank { null }
    ).joinToString(" · ")

    Box(
            modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(scrim)
                    .clickable(
                            onClickLabel = stringResource(R.string.genre_see_details),
                            role = Role.Button,
                            onClick = onClick
                    )
    ) {
        MovieArtwork(
                imagePath = movie.backdropPath ?: movie.posterPath,
                imageType = if (movie.backdropPath == null) ImageType.POSTER else ImageType.BACKDROP,
                modifier = Modifier.matchParentSize()
        )
        Box(
                modifier = Modifier
                        .matchParentSize()
                        .background(discoverHeroGradient(scrim))
        )

        movie.voteAverage?.let { rating ->
            Text(
                    text = stringResource(R.string.discover_rating_badge, formatMovieRating(rating)),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(scrim, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }

        Column(
                modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 48.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Text(
                    text = movie.title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
            )
            if (metadata.isNotEmpty()) {
                Text(
                        text = metadata,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))
            Button(
                    onClick = onTrailerClick,
                    enabled = !isTrailerLoading,
                    shape = RoundedCornerShape(12.dp)
            ) {
                if (isTrailerLoading) {
                    CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(text = stringResource(R.string.watch_trailer))
            }
        }
    }
}

@Preview(widthDp = 390)
@Composable
private fun TrendingSectionPreview() {
    DiscoverPreview {
        TrendingSection(
                movies = UiState.Success(listOf(MovieDomainModel(
                movieId = 1,
                title = "Dune: Part Two",
                posterPath = null,
                releaseDate = "March 1, 2024",
                releaseYear = "2024",
                voteAverage = 8.8
        ))),
                genreNames = emptyMap(),
                isTrailerLoading = false,
                onRetry = {},
                onMovieClicked = {},
                onWatchTrailer = {},
                contentInset = 18.dp
        )
    }
}

@Preview(widthDp = 390)
@Composable
private fun TrendingMovieCardPreview() {
    DiscoverPreview {
        TrendingMovieCard(
                movie = MovieDomainModel(
                movieId = 1,
                title = "Dune: Part Two",
                posterPath = null,
                releaseDate = "March 1, 2024",
                releaseYear = "2024",
                voteAverage = 8.8
        ),
                genreNames = listOf("Science Fiction", "Adventure"),
                isTrailerLoading = false,
                onClick = {},
                onTrailerClick = {}
        )
    }
}

package com.aldiprahasta.tmdb.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.ui.components.discoverHeroGradient
import com.aldiprahasta.tmdb.ui.components.ShimmerEffect
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

@Composable
internal fun DashboardSectionTitle(title: String, contentInset: Dp) {
    Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = contentInset).semantics { heading() }
    )
}

@Composable
internal fun DashboardSkeleton(height: Dp, contentInset: Dp) {
    ShimmerEffect(
            modifier = Modifier
                    .padding(horizontal = contentInset)
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(12.dp))
    )
}

@Composable
internal fun DashboardSectionMessage(contentInset: Dp, onRetry: (() -> Unit)? = null) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = contentInset),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val message = if (onRetry == null) R.string.discover_no_results else R.string.discover_load_error
        Text(
                text = stringResource(message),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
        )
        if (onRetry != null) {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.discover_retry))
            }
        }
    }
}

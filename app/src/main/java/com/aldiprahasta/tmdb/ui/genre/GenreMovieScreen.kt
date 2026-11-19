package com.aldiprahasta.tmdb.ui.genre

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.ui.theme.DiscoverRating
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.formatMovieRating
import com.aldiprahasta.tmdb.utils.setupPagingLoadState
import org.koin.androidx.compose.koinViewModel

@Composable
fun GenreMovieScreen(
        genreName: String,
        onBackPressed: () -> Unit,
        onMovieClicked: (movieId: Int) -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: GenreMovieViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val movies = viewModel.movies.collectAsLazyPagingItems()

    DiscoverSystemBars()
    DiscoverTheme {
        GenreMovieContent(
                genreName = genreName,
                uiState = uiState,
                movies = movies,
                onEvent = viewModel::onEvent,
                onBackPressed = onBackPressed,
                onMovieClicked = onMovieClicked,
                modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenreMovieContent(
        genreName: String,
        uiState: GenreMovieState,
        movies: LazyPagingItems<MovieDomainModel>,
        onEvent: (GenreMovieEvent) -> Unit,
        onBackPressed: () -> Unit,
        onMovieClicked: (Int) -> Unit,
        modifier: Modifier = Modifier
) {
    Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    text = stringResource(R.string.genre_movies_title, genreName),
                                    style = MaterialTheme.typography.titleLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackPressed) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.discover_back)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                        )
                )
            },
            modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            GenreMovieFilterBar(
                    genreName = genreName,
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            key(uiState) {
                GenreMovieList(
                        movies = movies,
                        onMovieClicked = onMovieClicked,
                        modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GenreMovieList(
        movies: LazyPagingItems<MovieDomainModel>,
        onMovieClicked: (Int) -> Unit,
        modifier: Modifier = Modifier
) {
    val refresh = movies.loadState.refresh

    when {
        movies.itemCount == 0 && refresh is LoadState.Loading -> LoadingScreen(modifier)
        movies.itemCount == 0 && refresh is LoadState.Error -> Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.discover_load_error))
                TextButton(onClick = movies::retry) {
                    Text(stringResource(R.string.discover_retry))
                }
            }
        }
        movies.itemCount == 0 -> Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.discover_no_results))
        }
        else -> LazyColumn(
                modifier = modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(count = movies.itemCount, key = movies.itemKey { it.movieId }) { index ->
                movies[index]?.let { movie ->
                    GenreMovieCard(movie = movie, onClick = { onMovieClicked(movie.movieId) })
                }
            }
            setupPagingLoadState(movies)
        }
    }
}

@Composable
private fun GenreMovieCard(movie: MovieDomainModel, onClick: () -> Unit) {
    Surface(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
    ) {
        Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MovieArtwork(
                    imagePath = movie.posterPath,
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
                        text = movie.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                )
                movie.releaseYear?.let { year ->
                    Text(
                            text = year,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                movie.voteAverage?.let { rating ->
                    Text(
                            text = stringResource(R.string.discover_rating_badge, formatMovieRating(rating)),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = DiscoverRating
                    )
                }
                if (movie.overview.isNotBlank()) {
                    Text(
                            text = movie.overview,
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
    }
}

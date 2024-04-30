package com.aldiprahasta.tmdb.ui.movie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.ui.components.ContentItem
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieScreen(onMovieClicked: (movieId: Int) -> Unit, modifier: Modifier = Modifier) {
    val viewModel: MovieViewModel = koinViewModel()
    val popularMovieList by viewModel.popularMovieList.collectAsStateWithLifecycle()

    MovieContent(
            popularMovieList = popularMovieList,
            modifier = modifier,
            onItemClicked = { movieId ->
                onMovieClicked(movieId)
            })
}

@Composable
fun MovieContent(
        popularMovieList: UiState<List<MovieDomainModel>>,
        onItemClicked: (movieId: Int) -> Unit,
        modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        popularMovieList.doIfSuccess { movieList ->
            LazyColumn(contentPadding = PaddingValues(10.dp)) {
                itemsIndexed(movieList) { index, movie ->
                    ContentItem(
                            title = movie.title,
                            releaseDate = movie.releaseDate,
                            posterPath = movie.posterPath,
                            characterName = null,
                            onItemClicked = { onItemClicked(movie.movieId) }
                    )

                    if (index < movieList.lastIndex) {
                        HorizontalDivider(Modifier.padding(vertical = 16.dp))
                    }
                }
            }
        }

        popularMovieList.doIfError { _, _ ->
            ErrorScreen(errorMessage = "No Movies Found")
        }

        popularMovieList.doIfLoading {
            LoadingScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieContentPreview() {
    MovieContent(
            popularMovieList = UiState.Success(listOf(
                    MovieDomainModel(
                            "Dune: Part Two",
                            "",
                            "24 December 2023",
                            12312
                    ),
                    MovieDomainModel(
                            "Dune: Part Two",
                            "",
                            "24 December 2023",
                            12312
                    ),
                    MovieDomainModel(
                            "Dune: Part Two",
                            "",
                            "24 December 2023",
                            12312
                    )
            )),
            onItemClicked = {},
    )
}
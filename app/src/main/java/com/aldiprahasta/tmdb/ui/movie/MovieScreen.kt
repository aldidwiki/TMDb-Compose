package com.aldiprahasta.tmdb.ui.movie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = Color.White,
                        ),
                        title = {
                            Text(text = "TMDb")
                        },
                        scrollBehavior = scrollBehavior
                )
            }
    ) { innerPadding ->
        val viewModel: MovieViewModel = koinViewModel()
        val popularMovieList by viewModel.popularMovieList.collectAsStateWithLifecycle()

        MovieContent(
                popularMovieList = popularMovieList,
                modifier = Modifier.padding(innerPadding),
                onItemClicked = { movieId ->
                    // TODO movie detail
                })
    }
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
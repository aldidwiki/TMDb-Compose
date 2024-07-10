package com.aldiprahasta.tmdb.ui.movie

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.ui.components.ContentItem
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.utils.setupPagingLoadState
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieScreen(onMovieClicked: (movieId: Int) -> Unit, modifier: Modifier = Modifier) {
    val viewModel: MovieViewModel = koinViewModel()
    val popularMoviePagingItems = viewModel.popularMovieList.collectAsLazyPagingItems()

    MovieContent(
            popularMoviePagingItems = popularMoviePagingItems,
            modifier = modifier,
            onItemClicked = { movieId ->
                onMovieClicked(movieId)
            })
}

@Composable
fun MovieContent(
        popularMoviePagingItems: LazyPagingItems<MovieDomainModel>,
        onItemClicked: (movieId: Int) -> Unit,
        modifier: Modifier = Modifier
) {
    LazyColumn(
            contentPadding = PaddingValues(10.dp),
            modifier = modifier
    ) {
        items(popularMoviePagingItems.itemCount) { index ->
            popularMoviePagingItems[index]?.let { movie ->
                ContentItem(
                        title = movie.title,
                        releaseDate = movie.releaseDate,
                        posterPath = movie.posterPath,
                        characterName = null,
                        onItemClicked = { onItemClicked(movie.movieId) },
                        totalEpisodeCount = null
                )

                if (index < popularMoviePagingItems.itemCount - 1) {
                    HorizontalDivider(Modifier.padding(vertical = 16.dp))
                }
            } ?: run {
                ErrorScreen(
                        errorMessage = "No Movies Found",
                        modifier = Modifier.fillParentMaxSize()
                )
            }
        }

        setupPagingLoadState(popularMoviePagingItems)
    }
}

@Preview(showBackground = true)
@Composable
fun MovieContentPreview() {
    MovieContent(
            popularMoviePagingItems = flowOf(PagingData.from(listOf(
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
            ))).collectAsLazyPagingItems(),
            onItemClicked = {},
    )
}
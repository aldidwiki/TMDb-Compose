package com.aldiprahasta.tmdb.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverMediaCard
import com.aldiprahasta.tmdb.ui.components.DiscoverPersonCard
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.setupPagingLoadState
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.UiState

@Composable
internal fun SearchResultList(
        feed: UiState<SearchResultFeed>?,
        movieGenreNames: Map<Int, String>,
        onRetry: () -> Unit,
        onItemClicked: (SearchDomainModel) -> Unit,
        modifier: Modifier = Modifier
) {
    when (feed) {
        null, UiState.Loading -> LoadingScreen(modifier)
        is UiState.Error -> SearchStatus(
                message = stringResource(R.string.search_load_error),
                onRetry = onRetry,
                modifier = modifier
        )

        is UiState.Success -> {
            if (feed.data.totalResults == 0) {
                SearchStatus(message = stringResource(R.string.search_no_results), modifier = modifier)
                return
            }

            val results = feed.data.results.collectAsLazyPagingItems()
            val refresh = results.loadState.refresh
            when {
                results.itemCount == 0 && refresh is LoadState.Loading -> LoadingScreen(modifier)
                results.itemCount == 0 && refresh is LoadState.Error -> SearchStatus(
                        message = stringResource(R.string.search_load_error),
                        onRetry = results::retry,
                        modifier = modifier
                )
                results.itemCount == 0 -> SearchStatus(
                        message = stringResource(R.string.search_no_results),
                        modifier = modifier
                )
                else -> LazyColumn(
                        modifier = modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // TMDb can repeat a result across pages, so use the default positional keys.
                    items(count = results.itemCount) { index ->
                        results[index]?.let { result ->
                            SearchResultCard(result, movieGenreNames, onClick = { onItemClicked(result) })
                        }
                    }
                    setupPagingLoadState(results)
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(result: SearchDomainModel, movieGenreNames: Map<Int, String>, onClick: () -> Unit) {
    if (result.mediaType == Constant.PERSON_MEDIA_TYPE) {
        val person = stringResource(R.string.search_type_person)
        DiscoverPersonCard(
                name = result.name,
                subtitle = if (result.knownFor.isBlank()) person
                else stringResource(R.string.search_result_subtitle, person, result.knownFor),
                profilePath = result.imagePath,
                onClick = onClick
        )
    } else {
        val isMovie = result.mediaType == Constant.MOVIE_MEDIA_TYPE
        val mediaLabel = stringResource(if (isMovie) R.string.search_type_movie else R.string.search_category_tv)
        val genres = if (isMovie) result.genreIds.mapNotNull(movieGenreNames::get).take(2).joinToString(" / ")
        else ""

        DiscoverMediaCard(
                title = result.name,
                subtitle = result.releaseYear?.let { stringResource(R.string.search_result_subtitle, mediaLabel, it) }
                        ?: mediaLabel,
                posterPath = result.imagePath,
                voteAverage = result.voteAverage,
                description = genres.ifBlank { result.overview },
                onClick = onClick
        )
    }
}

@Composable
private fun SearchStatus(message: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
    Box(modifier = modifier.fillMaxWidth().padding(18.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (onRetry != null) {
                TextButton(onClick = onRetry) { Text(stringResource(R.string.discover_retry)) }
            }
        }
    }
}

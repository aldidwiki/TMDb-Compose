package com.aldiprahasta.tmdb.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverMediaCard
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.setupPagingLoadState

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
        SearchPersonCard(result, onClick)
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
private fun SearchPersonCard(result: SearchDomainModel, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    Surface(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.outline),
            modifier = Modifier.fillMaxWidth()
    ) {
        Row(
                modifier = Modifier.heightIn(min = 108.dp).padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            MovieArtwork(
                    imagePath = result.imagePath,
                    imageType = ImageType.PROFILE,
                    modifier = Modifier.size(64.dp).clip(CircleShape),
                    placeholder = { modifier ->
                        val initials = result.name.trim().split(Regex("\\s+")).take(2)
                                .joinToString("") { it.take(1).uppercase() }
                        Box(
                                modifier = modifier.background(colors.primaryContainer),
                                contentAlignment = Alignment.Center
                        ) {
                            Text(
                                    text = initials,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.primary
                            )
                        }
                    }
            )
            Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                        text = result.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                )
                val person = stringResource(R.string.search_type_person)
                Text(
                        text = if (result.knownFor.isBlank()) person
                        else stringResource(R.string.search_result_subtitle, person, result.knownFor),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
            )
        }
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

package com.aldiprahasta.tmdb.ui.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ExploreItem
import com.aldiprahasta.tmdb.domain.model.ExploreTab
import com.aldiprahasta.tmdb.ui.components.DiscoverMediaCard
import com.aldiprahasta.tmdb.ui.components.DiscoverPersonCard
import com.aldiprahasta.tmdb.ui.components.ShimmerEffect
import com.aldiprahasta.tmdb.utils.MediaType

@Composable
internal fun ExploreResults(
        results: LazyPagingItems<ExploreItem>,
        state: ExploreState,
        listState: LazyListState,
        title: String,
        loading: Boolean,
        onReset: () -> Unit,
        onItemClicked: (Int, MediaType) -> Unit,
        modifier: Modifier = Modifier
) {
    val isPeople = state.tab == ExploreTab.PEOPLE
    val movies = remember(state.movieGenres) { state.movieGenreNames }
    val shows = remember(state.tvGenres) { state.tvGenreNames }
    LazyColumn(
            state = listState,
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = ExploreInset, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "title") {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
        }
        val refresh = results.loadState.refresh
        when {
            loading || refresh is LoadState.Loading -> {
                item { Text(stringResource(R.string.explore_loading), style = MaterialTheme.typography.bodySmall) }
                items(3) {
                    ShimmerEffect(
                            Modifier.fillMaxWidth().height(if (isPeople) 108.dp else 154.dp)
                                    .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
            results.itemCount == 0 && refresh is LoadState.Error -> item {
                ExploreResultMessage(
                        title = stringResource(R.string.explore_error_title),
                        description = stringResource(R.string.explore_error),
                        action = stringResource(R.string.discover_retry),
                        onAction = results::retry
                )
            }
            results.itemCount == 0 -> item {
                ExploreResultMessage(
                        title = stringResource(R.string.explore_no_results),
                        description = stringResource(R.string.explore_empty_help),
                        action = stringResource(R.string.explore_reset_empty),
                        onAction = onReset
                )
            }
            else -> {
                items(count = results.itemCount, key = results.itemKey { it.key }) { index ->
                    results[index]?.let {
                        ExploreResultCard(
                                item = it,
                                genreNames = if (it.mediaType == MediaType.TV_TYPE) shows else movies,
                                onClick = { onItemClicked(it.id, it.mediaType) }
                        )
                    }
                }
                when (results.loadState.append) {
                    LoadState.Loading -> item {
                        Column(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                    stringResource(if (isPeople) R.string.explore_loading_more_people else R.string.explore_loading_more),
                                    style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    is LoadState.Error -> item {
                        ExploreResultMessage(
                                title = stringResource(if (isPeople) R.string.explore_more_people_error else R.string.explore_more_error),
                                action = stringResource(R.string.discover_retry),
                                onAction = results::retry
                        )
                    }
                    is LoadState.NotLoading -> Unit
                }
            }
        }
    }
}

@Composable
private fun ExploreResultCard(item: ExploreItem, genreNames: Map<Int, String>, onClick: () -> Unit) {
    val badge = stringResource(when (item.mediaType) {
        MediaType.MOVIE_TYPE -> R.string.explore_movie_badge
        MediaType.TV_TYPE -> R.string.explore_tv_badge
        MediaType.PERSON_TYPE -> R.string.explore_person_badge
    })

    if (item.mediaType == MediaType.PERSON_TYPE) {
        val person = stringResource(R.string.search_type_person)
        DiscoverPersonCard(
                name = item.title,
                subtitle = listOfNotNull(person, item.department?.takeIf(String::isNotBlank)).joinToString(" · "),
                profilePath = item.imagePath,
                onClick = onClick,
                titleBadge = { ExploreBadge(badge) },
                trailingContent = {}
        )
    } else {
        val genres = item.genreIds.mapNotNull(genreNames::get).take(2).joinToString(" / ").takeIf(String::isNotBlank)
        val year = item.releaseDate?.take(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) }
        DiscoverMediaCard(
                title = item.title,
                subtitle = listOfNotNull(year, genres).joinToString(" · "),
                posterPath = item.imagePath,
                voteAverage = item.rating,
                description = item.overview,
                onClick = onClick,
                titleBadge = { ExploreBadge(badge) }
        )
    }
}

@Composable
private fun ExploreBadge(text: String) {
    val colors = MaterialTheme.colorScheme
    Surface(
            shape = RoundedCornerShape(20.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.outline)
    ) {
        Text(
                text,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
        )
    }
}

@Composable
private fun ExploreResultMessage(title: String, action: String, onAction: () -> Unit, description: String? = null) {
    val colors = MaterialTheme.colorScheme
    Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.outline)
    ) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
            if (description != null) {
                Text(description, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
            }
            OutlinedButton(onClick = onAction, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Text(action)
            }
        }
    }
}

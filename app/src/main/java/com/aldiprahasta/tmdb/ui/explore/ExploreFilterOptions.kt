package com.aldiprahasta.tmdb.ui.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ExploreDepartment
import com.aldiprahasta.tmdb.domain.model.ExploreFilters
import com.aldiprahasta.tmdb.domain.model.ExploreKnownFor
import com.aldiprahasta.tmdb.domain.model.ExploreTab
import com.aldiprahasta.tmdb.domain.model.ExploreYears
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.formatMovieRating

@Composable
internal fun ColumnScope.ExploreFilterOptions(
        state: ExploreState,
        draft: ExploreFilters,
        onDraftChanged: (ExploreFilters) -> Unit,
        onChooseYear: () -> Unit,
        onDismiss: () -> Unit,
        onEvent: (ExploreEvent) -> Unit
) {
    val isPeople = state.tab == ExploreTab.PEOPLE

    FilterGroup(stringResource(R.string.explore_content_type)) {
        FilterChips {
            ExploreTab.entries.filter { it != ExploreTab.ALL }.forEach { tab ->
                ExploreChip(stringResource(tab.label), selected = tab == state.tab, onClick = {
                    onEvent(ExploreEvent.SelectTab(tab))
                    onDismiss()
                })
            }
        }
    }

    if (isPeople) {
        FilterGroup(stringResource(R.string.explore_department)) {
            FilterChips {
                ExploreDepartment.entries.forEach { department ->
                    ExploreChip(stringResource(department.label), department in draft.departments, onClick = {
                        onDraftChanged(draft.copy(departments = draft.departments.toggle(department)))
                    })
                }
            }
        }
        FilterGroup(stringResource(R.string.explore_known_for)) {
            FilterChips {
                ExploreKnownFor.entries.forEach { type ->
                    ExploreChip(stringResource(type.label), type == draft.knownFor, onClick = {
                        onDraftChanged(draft.copy(knownFor = type.takeUnless { draft.knownFor == it }))
                    })
                }
            }
        }
    } else {
        val currentGenres = state.genreOptions()
        val catalogLoading = state.genreStates().any { it is UiState.Loading }
        val chosenGenres = draft.genres.map { it.id }.toSet()
        val displayedGenres = (currentGenres + draft.genres).distinctBy { it.id }.sortedBy { it.name }

        FilterGroup(stringResource(R.string.explore_genre)) {
            FilterChips {
                displayedGenres.forEach { genre ->
                    ExploreChip(genre.name, genre.id in chosenGenres, enabled = !catalogLoading, onClick = {
                        val selected = draft.genres.filterNot { it.id == genre.id }.toSet()
                        onDraftChanged(draft.copy(genres = if (genre.id in chosenGenres) selected else selected + genre))
                    })
                }
            }
            if (catalogLoading) {
                Text(stringResource(R.string.explore_genres_loading), style = MaterialTheme.typography.bodySmall)
            }
            if (state.genreStates().any { it is UiState.Error }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.explore_genres_error), style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { onEvent(ExploreEvent.RetryGenres) }) {
                        Text(stringResource(R.string.discover_retry))
                    }
                }
            }
        }
        FilterGroup(stringResource(R.string.genre_release_year)) {
            val years = draft.years
            val action = stringResource(when {
                years == null -> R.string.explore_year_choose
                years.from == years.to -> R.string.explore_year_change
                else -> R.string.explore_year_change_range
            })
            Surface(
                    onClick = onChooseYear,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                            text = years.label() + " · " + action,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelLarge
                    )
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
        FilterGroup(stringResource(R.string.genre_minimum_rating)) {
            FilterChips {
                (6..9).forEach { rating ->
                    ExploreChip(formatMovieRating(rating.toDouble()) + "+", draft.minimumRating == rating, onClick = {
                        onDraftChanged(draft.copy(minimumRating = rating.takeUnless { draft.minimumRating == it }))
                    })
                }
            }
        }
    }

    Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedButton(
                onClick = {
                    onEvent(ExploreEvent.ResetFilters)
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
        ) { Text(stringResource(R.string.explore_filter_reset), textAlign = TextAlign.Center) }
        Button(
                onClick = {
                    val available = state.genreOptions().associateBy { it.id }
                    onEvent(ExploreEvent.ApplyFilters(draft.copy(genres = draft.genres.map { available[it.id] ?: it }.toSet())))
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
        ) { Text(stringResource(R.string.explore_filter_apply), textAlign = TextAlign.Center) }
    }
}

@Composable
private fun FilterGroup(label: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        content()
    }
}

@Composable
private fun FilterChips(content: @Composable () -> Unit) {
    FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
    ) { content() }
}

private fun <T> Set<T>.toggle(value: T): Set<T> = if (value in this) this - value else this + value

@Preview(widthDp = 390)
@Composable
private fun ExploreMovieFilterOptionsPreview() {
    DiscoverPreview {
        val genres = UiState.Success(listOf(GenreDomainModel(28, "Action"), GenreDomainModel(878, "Science Fiction")))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ExploreFilterOptions(
                    state = ExploreState(tab = ExploreTab.MOVIES, movieGenres = genres),
                    draft = ExploreFilters(years = ExploreYears(2024), minimumRating = 7),
                    onDraftChanged = {},
                    onChooseYear = {},
                    onDismiss = {},
                    onEvent = {}
            )
        }
    }
}

@Preview(widthDp = 390)
@Composable
private fun ExplorePeopleFilterOptionsPreview() {
    DiscoverPreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ExploreFilterOptions(
                    state = ExploreState(tab = ExploreTab.PEOPLE),
                    draft = ExploreFilters(departments = setOf(ExploreDepartment.ACTING)),
                    onDraftChanged = {},
                    onChooseYear = {},
                    onDismiss = {},
                    onEvent = {}
            )
        }
    }
}

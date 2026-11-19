package com.aldiprahasta.tmdb.ui.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ExploreDepartment
import com.aldiprahasta.tmdb.domain.model.ExploreKnownFor
import com.aldiprahasta.tmdb.domain.model.ExploreQuery
import com.aldiprahasta.tmdb.domain.model.ExploreTab
import com.aldiprahasta.tmdb.ui.components.DiscoverBottomSheet
import com.aldiprahasta.tmdb.ui.components.DiscoverOptionsSheet
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.formatMovieRating

@Composable
internal fun ExploreChip(
        label: String,
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        clickRole: Role = Role.Button,
        icon: ImageVector? = null
) {
    val colors = MaterialTheme.colorScheme
    Surface(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.semantics {
                this.selected = selected
                role = clickRole
            },
            shape = RoundedCornerShape(24.dp),
            color = if (selected) colors.primary else colors.surface,
            contentColor = if (selected) colors.onPrimary else colors.onSurfaceVariant,
            border = BorderStroke(1.dp, if (selected) colors.primary else colors.outline)
    ) {
        Row(
                modifier = Modifier.heightIn(min = 32.dp).padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
internal fun ExploreSortDrawer(query: ExploreQuery, onDismiss: () -> Unit, onEvent: (ExploreEvent) -> Unit) {
    DiscoverOptionsSheet(
            title = stringResource(R.string.explore_sort),
            description = stringResource(
                    if (query.tab == ExploreTab.PEOPLE) R.string.explore_sort_people_description else R.string.explore_sort_description
            ),
            applyLabel = stringResource(R.string.explore_sort_apply),
            options = query.tab.sorts(),
            appliedOption = query.order,
            optionLabel = { stringResource(it.label) },
            onDismiss = onDismiss,
            onApply = {
                onEvent(ExploreEvent.ApplySort(it))
                onDismiss()
            }
    )
}

@Composable
internal fun ExploreFilterDrawer(state: ExploreState, onDismiss: () -> Unit, onEvent: (ExploreEvent) -> Unit) {
    var draft by remember(state.query.filters) { mutableStateOf(state.query.filters) }
    var showYearPicker by remember { mutableStateOf(false) }
    val isPeople = state.tab == ExploreTab.PEOPLE

    if (showYearPicker) {
        ExploreYearPicker(
                years = draft.years,
                onDismiss = { showYearPicker = false },
                onApply = { year ->
                    draft = draft.copy(years = year)
                    showYearPicker = false
                }
        )
        return
    }

    DiscoverBottomSheet(
            title = stringResource(state.tab.filterTitle),
            description = stringResource(
                    if (isPeople) R.string.explore_filter_people_description else R.string.explore_filter_description
            ),
            onDismiss = onDismiss
    ) {
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
                            draft = draft.copy(departments = draft.departments.toggle(department))
                        })
                    }
                }
            }
            FilterGroup(stringResource(R.string.explore_known_for)) {
                FilterChips {
                    ExploreKnownFor.entries.forEach { type ->
                        ExploreChip(stringResource(type.label), type == draft.knownFor, onClick = {
                            draft = draft.copy(knownFor = type.takeUnless { draft.knownFor == it })
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
                            draft = draft.copy(genres = if (genre.id in chosenGenres) selected else selected + genre)
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
                        onClick = { showYearPicker = true },
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
                            draft = draft.copy(minimumRating = rating.takeUnless { draft.minimumRating == it })
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

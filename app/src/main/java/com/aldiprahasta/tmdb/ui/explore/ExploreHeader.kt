package com.aldiprahasta.tmdb.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ExploreKnownFor
import com.aldiprahasta.tmdb.domain.model.ExplorePreset
import com.aldiprahasta.tmdb.domain.model.ExploreQuery
import com.aldiprahasta.tmdb.domain.model.ExploreTab
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import com.aldiprahasta.tmdb.utils.formatMovieRating

@Composable
internal fun ExploreHeader(
        query: ExploreQuery,
        title: String,
        status: String?,
        onEvent: (ExploreEvent) -> Unit,
        onFilter: () -> Unit,
        onSort: () -> Unit
) {
    val isFiltered = query.filters.hasTitleFilters || query.filters.hasPeopleFilters
    val subtitle = status ?: when {
        isFiltered -> filterSummary(query)
        query.sort != null -> stringResource(R.string.explore_sorted_by, stringResource(query.order.label))
        else -> stringResource(query.tab.subtitle)
    }

    Column(Modifier.fillMaxWidth().padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(Modifier.padding(horizontal = ExploreInset), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = ExploreInset).selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ExploreTab.entries.forEach { tab ->
                ExploreChip(
                        label = stringResource(tab.label),
                        selected = tab == query.tab,
                        onClick = { onEvent(ExploreEvent.SelectTab(tab)) },
                        modifier = Modifier.weight(1f),
                        clickRole = Role.Tab
                )
            }
        }

        val quickOptions = query.tab.presets()
        if (quickOptions.isEmpty()) {
            Text(
                    text = stringResource(R.string.explore_browse_all),
                    modifier = Modifier.padding(horizontal = ExploreInset, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = ExploreInset),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickOptions, key = { it.name }) { quick ->
                    val selected = if (query.tab == ExploreTab.PEOPLE && query.sort != null) quick.defaultSort == query.sort
                    else query.preset == quick
                    ExploreChip(
                            label = stringResource(quick.label),
                            selected = selected,
                            onClick = { onEvent(ExploreEvent.SelectPreset(quick)) }
                    )
                }
            }
        }

        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = ExploreInset),
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ExploreChip(
                    label = stringResource(R.string.explore_filter),
                    selected = isFiltered,
                    onClick = onFilter,
                    icon = Icons.Default.FilterList
            )
            ExploreChip(
                    label = stringResource(query.sort?.shortLabel ?: R.string.explore_sort),
                    selected = query.sort != null,
                    onClick = onSort,
                    icon = Icons.Default.SwapVert
            )
        }
    }
}

@Composable
private fun filterSummary(query: ExploreQuery): String {
    val filters = query.filters
    val parts = mutableListOf<String>()
    parts += filters.genres.map { it.name }
    filters.years?.let { parts += it.label() }
    filters.minimumRating?.let { parts += formatMovieRating(it.toDouble()) + "+" }
    filters.departments.forEach { parts += stringResource(it.label) }
    filters.knownFor?.let {
        parts += stringResource(if (it == ExploreKnownFor.BOTH) R.string.explore_known_both_summary else it.label)
    }
    return parts.joinToString(" · ")
}

@Preview(widthDp = 390)
@Composable
private fun ExploreHeaderPreview() {
    DiscoverPreview {
        ExploreHeader(
                query = ExploreQuery(tab = ExploreTab.MOVIES, preset = ExplorePreset.NOW_PLAYING),
                title = "Now Playing",
                status = null,
                onEvent = {},
                onFilter = {},
                onSort = {}
        )
    }
}

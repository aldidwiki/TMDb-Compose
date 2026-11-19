package com.aldiprahasta.tmdb.ui.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ExploreQuery
import com.aldiprahasta.tmdb.domain.model.ExploreTab
import com.aldiprahasta.tmdb.ui.components.DiscoverBottomSheet
import com.aldiprahasta.tmdb.ui.components.DiscoverOptionsSheet
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview

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
        ExploreFilterOptions(
                state = state,
                draft = draft,
                onDraftChanged = { draft = it },
                onChooseYear = { showYearPicker = true },
                onDismiss = onDismiss,
                onEvent = onEvent
        )
    }
}


@Preview(widthDp = 390)
@Composable
private fun ExploreChipPreview() {
    DiscoverPreview {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ExploreChip(label = "Movies", selected = true, onClick = {})
            ExploreChip(label = "TV Shows", selected = false, onClick = {})
        }
    }
}

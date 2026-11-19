package com.aldiprahasta.tmdb.ui.genre

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.MovieSortOrder
import com.aldiprahasta.tmdb.ui.components.DiscoverBottomSheet

@get:StringRes
internal val MovieSortOrder.label: Int
    get() = when (this) {
        MovieSortOrder.POPULARITY -> R.string.genre_sort_popularity
        MovieSortOrder.NEWEST -> R.string.genre_sort_newest
        MovieSortOrder.HIGHEST_RATED -> R.string.genre_sort_highest_rated
        MovieSortOrder.TITLE -> R.string.genre_sort_title
        MovieSortOrder.MOST_VOTED -> R.string.genre_sort_most_voted
    }

@Composable
internal fun <T> GenreFilterSheet(
        title: String,
        description: String,
        applyLabel: String,
        options: List<T>,
        appliedOption: T,
        optionLabel: @Composable (T) -> String,
        onDismiss: () -> Unit,
        onApply: (T) -> Unit
) {
    var selectedOption by rememberSaveable(appliedOption) { mutableStateOf(appliedOption) }

    DiscoverBottomSheet(
            title = title,
            description = description,
            onDismiss = onDismiss
    ) {
        Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                GenreFilterOption(
                        label = optionLabel(option),
                        selected = selectedOption == option,
                        onClick = { selectedOption = option }
                )
            }
        }
        Button(
                onClick = { onApply(selectedOption) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp)
        ) {
            Text(applyLabel)
        }
    }
}

@Composable
private fun GenreFilterOption(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)

    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(shape)
                    .background(if (selected) colors.primaryContainer else colors.surface)
                    .border(BorderStroke(1.dp, if (selected) colors.primary else colors.outline), shape)
                    .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) colors.primary else colors.onSurface
        )
        if (selected) {
            Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(16.dp)
            )
        } else {
            Box(
                    modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(10.dp)
                            .border(1.dp, colors.onSurfaceVariant, CircleShape)
            )
        }
    }
}

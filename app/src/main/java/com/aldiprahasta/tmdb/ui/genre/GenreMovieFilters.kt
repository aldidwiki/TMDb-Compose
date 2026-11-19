package com.aldiprahasta.tmdb.ui.genre

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.MovieSortOrder
import com.aldiprahasta.tmdb.utils.formatMovieRating
import java.util.Calendar

private enum class MovieFilter {
    SORT,
    RELEASE_YEAR,
    MINIMUM_RATING
}

@Composable
internal fun GenreMovieFilterBar(
        genreName: String,
        uiState: GenreMovieState,
        onEvent: (GenreMovieEvent) -> Unit,
        modifier: Modifier = Modifier
) {
    var activeFilter by rememberSaveable { mutableStateOf<MovieFilter?>(null) }
    val dismissFilter = { activeFilter = null }

    LazyRow(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "sort") {
            GenreFilterChip(
                    label = stringResource(uiState.sortOrder.label),
                    actionLabel = stringResource(R.string.genre_sort_by),
                    onClick = { activeFilter = MovieFilter.SORT },
                    primary = true
            )
        }
        item(key = "releaseYear") {
            GenreFilterChip(
                    label = uiState.releaseYear?.let { stringResource(R.string.genre_release_selection, it) }
                            ?: stringResource(R.string.genre_release_year),
                    actionLabel = stringResource(R.string.genre_release_year),
                    onClick = { activeFilter = MovieFilter.RELEASE_YEAR }
            )
        }
        item(key = "minimumRating") {
            GenreFilterChip(
                    label = stringResource(R.string.genre_rating_selection, ratingLabel(uiState.minimumRating)),
                    actionLabel = stringResource(R.string.genre_minimum_rating),
                    onClick = { activeFilter = MovieFilter.MINIMUM_RATING }
            )
        }
    }

    when (activeFilter) {
        MovieFilter.SORT -> GenreFilterSheet(
                title = stringResource(R.string.genre_sort_by),
                description = stringResource(R.string.genre_sort_description, genreName),
                applyLabel = stringResource(R.string.genre_apply_sort),
                options = MovieSortOrder.entries,
                appliedOption = uiState.sortOrder,
                optionLabel = { order -> stringResource(order.label) },
                onDismiss = dismissFilter,
                onApply = { order ->
                    dismissFilter()
                    onEvent(GenreMovieEvent.ApplySort(order))
                }
        )

        MovieFilter.RELEASE_YEAR -> {
            val releaseYears = remember(uiState.releaseYear) {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val years = (currentYear downTo currentYear - 3).toList() + listOfNotNull(uiState.releaseYear)
                years.distinct().sortedDescending() + listOf<Int?>(null)
            }

            GenreFilterSheet(
                    title = stringResource(R.string.genre_release_year),
                    description = stringResource(R.string.genre_release_year_description),
                    applyLabel = stringResource(R.string.genre_apply_year),
                    options = releaseYears,
                    appliedOption = uiState.releaseYear,
                    optionLabel = { year -> year?.toString() ?: stringResource(R.string.genre_all_years) },
                    onDismiss = dismissFilter,
                    onApply = { year ->
                        dismissFilter()
                        onEvent(GenreMovieEvent.ApplyReleaseYear(year))
                    }
            )
        }

        MovieFilter.MINIMUM_RATING -> GenreFilterSheet(
                title = stringResource(R.string.genre_minimum_rating),
                description = stringResource(R.string.genre_minimum_rating_description),
                applyLabel = stringResource(R.string.genre_apply_rating),
                options = listOf(6, 7, 8, 9, null),
                appliedOption = uiState.minimumRating,
                optionLabel = { rating -> ratingLabel(rating) },
                onDismiss = dismissFilter,
                onApply = { rating ->
                    dismissFilter()
                    onEvent(GenreMovieEvent.ApplyMinimumRating(rating))
                }
        )

        null -> Unit
    }
}

@Composable
private fun ratingLabel(rating: Int?): String =
        if (rating == null) stringResource(R.string.genre_any_rating)
        else stringResource(R.string.genre_rating_threshold, formatMovieRating(rating.toDouble()))

@Composable
private fun GenreFilterChip(
        label: String,
        actionLabel: String,
        onClick: () -> Unit,
        primary: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(18.dp)

    Box(
            modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clickable(onClickLabel = actionLabel, role = Role.Button, onClick = onClick),
            contentAlignment = Alignment.Center
    ) {
        Surface(
                shape = shape,
                color = if (primary) colors.primary else colors.surface,
                contentColor = if (primary) colors.onPrimary else colors.onSurfaceVariant,
                border = if (primary) null else BorderStroke(1.dp, colors.outline)
        ) {
            Row(
                    modifier = Modifier.heightIn(min = 34.dp).padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, style = MaterialTheme.typography.labelMedium)
                if (primary) {
                    Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

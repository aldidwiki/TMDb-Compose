package com.aldiprahasta.tmdb.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.SearchCategory
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import com.aldiprahasta.tmdb.utils.UiState
import java.text.NumberFormat
import kotlinx.coroutines.flow.emptyFlow

@Composable
internal fun SearchCategoryChips(uiState: SearchState, onSelected: (SearchCategory) -> Unit) {
    val colors = MaterialTheme.colorScheme
    LazyRow(
            modifier = Modifier.fillMaxWidth().selectableGroup(),
            contentPadding = PaddingValues(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SearchCategory.entries, key = { it.name }) { category ->
            val selected = category == uiState.category
            val label = stringResource(when (category) {
                SearchCategory.ALL -> R.string.search_category_all
                SearchCategory.MOVIES -> R.string.search_category_movies
                SearchCategory.TV -> R.string.search_category_tv
                SearchCategory.PEOPLE -> R.string.search_category_people
            })
            val total = (uiState.feeds[category] as? UiState.Success)?.data?.totalResults

            Box(
                    modifier = Modifier.heightIn(min = 48.dp).selectable(
                            selected = selected,
                            onClick = { onSelected(category) },
                            role = Role.Tab
                    ),
                    contentAlignment = Alignment.Center
            ) {
                Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (selected) colors.primary else colors.surface,
                        contentColor = if (selected) colors.onPrimary else colors.onSurfaceVariant,
                        border = if (selected) null else BorderStroke(1.dp, colors.outline)
                ) {
                    Box(
                            modifier = Modifier.heightIn(min = 32.dp).padding(horizontal = 16.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                    ) {
                        Text(
                                text = total?.let { stringResource(R.string.search_chip_count, label, formatSearchCount(it)) }
                                        ?: label,
                                style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

internal fun formatSearchCount(count: Int): String = NumberFormat.getIntegerInstance().format(count)

@Preview(widthDp = 390)
@Composable
private fun SearchCategoryChipsPreview() {
    DiscoverPreview {
        SearchCategoryChips(
                uiState = SearchState(
                        category = SearchCategory.MOVIES,
                        feeds = mapOf(
                                SearchCategory.ALL to UiState.Success(SearchResultFeed(37, emptyFlow())),
                                SearchCategory.MOVIES to UiState.Success(SearchResultFeed(19, emptyFlow()))
                        )
                ),
                onSelected = {}
        )
    }
}

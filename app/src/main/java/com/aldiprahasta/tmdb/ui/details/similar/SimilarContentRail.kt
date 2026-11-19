package com.aldiprahasta.tmdb.ui.details.similar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.SimilarPage
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.details.DetailSectionHeader
import com.aldiprahasta.tmdb.utils.UiState

@Composable
internal fun SimilarContentRail(
        state: UiState<SimilarPage>,
        isTv: Boolean,
        onItemClicked: (Int) -> Unit,
        onSeeAll: () -> Unit,
        onRetry: () -> Unit
) {
    val items = remember(state) { (state as? UiState.Success)?.data?.items.orEmpty().distinctBy { it.id }.take(10) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        DetailSectionHeader(
                title = similarTitle(isTv),
                modifier = Modifier.padding(horizontal = 20.dp),
                onSeeAll = onSeeAll.takeIf { items.isNotEmpty() }
        )
        when (state) {
            UiState.Loading -> LoadingScreen(Modifier.fillMaxWidth().heightIn(min = 96.dp), indicatorSizeInDp = 28.dp)
            is UiState.Error -> DiscoverStatus(stringResource(R.string.discover_load_error), Modifier.fillMaxWidth(), onRetry)
            is UiState.Success -> if (items.isEmpty()) {
                DiscoverStatus(stringResource(R.string.detail_similar_empty), Modifier.fillMaxWidth())
            } else {
                LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        SimilarContentCard(
                                item = item,
                                showRating = false,
                                onClick = { onItemClicked(item.id) },
                                modifier = Modifier.width(108.dp)
                        )
                    }
                }
            }
        }
    }
}

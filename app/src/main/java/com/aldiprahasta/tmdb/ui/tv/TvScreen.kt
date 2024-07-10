package com.aldiprahasta.tmdb.ui.tv

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.aldiprahasta.tmdb.domain.model.TvDomainModel
import com.aldiprahasta.tmdb.ui.components.ContentItem
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.utils.setupPagingLoadState
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun TvScreen(
        onItemClicked: (tvId: Int) -> Unit,
        modifier: Modifier = Modifier
) {
    val tvViewModel: TvViewModel = koinViewModel()
    val popularTvPagingItems = tvViewModel.popularTv.collectAsLazyPagingItems()

    TvContent(
            popularTvPagingItems = popularTvPagingItems,
            onItemClicked = onItemClicked,
            modifier = modifier
    )
}

@Composable
fun TvContent(
        popularTvPagingItems: LazyPagingItems<TvDomainModel>,
        onItemClicked: (tvId: Int) -> Unit,
        modifier: Modifier = Modifier
) {
    LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(10.dp)
    ) {
        items(popularTvPagingItems.itemCount) { index ->
            popularTvPagingItems[index]?.let { popularTv ->
                ContentItem(
                        title = popularTv.title,
                        releaseDate = popularTv.releaseDate,
                        characterName = null,
                        posterPath = popularTv.posterPath,
                        onItemClicked = {
                            onItemClicked(popularTv.tvId)
                        },
                        totalEpisodeCount = null
                )

                if (index < popularTvPagingItems.itemCount - 1) {
                    HorizontalDivider(Modifier.padding(vertical = 16.dp))
                }
            } ?: run {
                ErrorScreen(
                        errorMessage = "No Tv Series Found",
                        modifier = Modifier.fillParentMaxSize()
                )
            }
        }

        setupPagingLoadState(popularTvPagingItems)
    }
}

@Preview(showBackground = true)
@Composable
fun TvContentPreview() {
    TvContent(
            popularTvPagingItems = flowOf(PagingData.from(listOf(
                    TvDomainModel(
                            posterPath = null,
                            tvId = 9989,
                            title = "omnesque",
                            releaseDate = "dicam"
                    ),
                    TvDomainModel(
                            posterPath = null,
                            tvId = 9989,
                            title = "omnesque",
                            releaseDate = "dicam"
                    ),
                    TvDomainModel(
                            posterPath = null,
                            tvId = 9989,
                            title = "omnesque",
                            releaseDate = "dicam"
                    ),
                    TvDomainModel(
                            posterPath = null,
                            tvId = 9989,
                            title = "omnesque",
                            releaseDate = "dicam"
                    )
            ))).collectAsLazyPagingItems(),
            onItemClicked = {}
    )
}

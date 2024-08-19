package com.aldiprahasta.tmdb.ui.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.ui.components.ContentItem
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.setupPagingLoadState
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
        onItemClicked: (contentId: Int, mediaType: String) -> Unit,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: SearchViewModel = koinViewModel()
    val searchResultsPagingItems = viewModel.searchResults.collectAsLazyPagingItems()

    SearchContent(
            searchQuery = viewModel.searchQuery,
            searchResultsPagingItems = searchResultsPagingItems,
            onSearchQueryChanged = { newQuery ->
                viewModel.onSearchQueryChange(newQuery)
            },
            onItemClicked = { contentId, mediaType ->
                onItemClicked(contentId, mediaType)
            },
            onBackPressed = onBackPressed,
            modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchContent(
        searchQuery: String,
        searchResultsPagingItems: LazyPagingItems<SearchDomainModel>,
        onSearchQueryChanged: (newQuery: String) -> Unit,
        onItemClicked: (contentId: Int, mediaType: String) -> Unit,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChanged,
            onSearch = {
                keyboardController?.hide()
            },
            active = true,
            onActiveChange = { state ->
                if (!state) onBackPressed()
            },
            placeholder = {
                Text(text = "Search Movie, Tv Show, and People")
            },
            leadingIcon = {
                Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null
                        )
                    }
                }
            },
            modifier = modifier
    ) {
        if (searchResultsPagingItems.itemCount < 1 && searchQuery.isNotEmpty()) {
            ErrorScreen(errorMessage = "No Data Found")
        } else LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
        ) {
            items(searchResultsPagingItems.itemCount) { index ->
                searchResultsPagingItems[index]?.let { searchResult ->
                    val subTitleText = when (searchResult.mediaType) {
                        Constant.TV_MEDIA_TYPE -> "${searchResult.releaseDate} | TV Show"

                        Constant.PERSON_MEDIA_TYPE -> searchResult.knownFor

                        else -> searchResult.releaseDate
                    }

                    ContentItem(
                            title = searchResult.name,
                            releaseDate = subTitleText,
                            characterName = null,
                            posterPath = searchResult.imagePath,
                            totalEpisodeCount = null,
                            onItemClicked = {
                                onItemClicked(searchResult.id, searchResult.mediaType)
                            }
                    )

                    if (index < searchResultsPagingItems.itemCount - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                }
            }

            setupPagingLoadState(searchResultsPagingItems)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchContentPreview(modifier: Modifier = Modifier) {
    SearchContent(
            searchQuery = "",
            onSearchQueryChanged = {},
            searchResultsPagingItems = flowOf(PagingData.from(listOf(
                    SearchDomainModel(
                            id = 6081,
                            name = "Daryl Shields",
                            imagePath = "dapibus",
                            releaseDate = "fugit",
                            mediaType = "menandri",
                            knownFor = "amet",
                            popularity = 2.3
                    ),
                    SearchDomainModel(
                            id = 6081,
                            name = "Daryl Shields",
                            imagePath = "dapibus",
                            releaseDate = "fugit",
                            mediaType = "menandri",
                            knownFor = "amet",
                            popularity = 2.3
                    ),
                    SearchDomainModel(
                            id = 6081,
                            name = "Daryl Shields",
                            imagePath = "dapibus",
                            releaseDate = "fugit",
                            mediaType = "menandri",
                            knownFor = "amet",
                            popularity = 2.3
                    ),
            ))).collectAsLazyPagingItems(),
            onItemClicked = { _, _ -> },
            onBackPressed = {}
    )
}

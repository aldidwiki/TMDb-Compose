package com.aldiprahasta.tmdb.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.SearchCategory
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.UiState
import java.text.NumberFormat
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
        onItemClicked: (contentId: Int, mediaType: String) -> Unit,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: SearchViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DiscoverSystemBars()
    DiscoverTheme {
        SearchContent(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onItemClicked = onItemClicked,
                onBackPressed = onBackPressed,
                modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchContent(
        uiState: SearchState,
        onEvent: (SearchEvent) -> Unit,
        onItemClicked: (Int, String) -> Unit,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val activeFeed = uiState.feeds[uiState.category]

    Scaffold(
            modifier = modifier.fillMaxSize().imePadding(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
            topBar = {
                TopAppBar(
                        title = {
                            Column {
                                Text(
                                        text = stringResource(R.string.dashboard_tab_search),
                                        style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                        text = stringResource(R.string.search_subtitle),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                focusManager.clearFocus()
                                onBackPressed()
                            }) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.discover_back)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                        )
                )
            }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            SearchField(
                    query = uiState.query,
                    onQueryChanged = { onEvent(SearchEvent.OnSearchQueryChanged(it)) },
                    onSearch = { focusManager.clearFocus() },
                    modifier = Modifier.padding(start = 18.dp, top = 12.dp, end = 18.dp, bottom = 6.dp)
            )

            if (uiState.query.isNotBlank()) {
                SearchCategoryChips(
                        uiState = uiState,
                        onSelected = { category ->
                            focusManager.clearFocus()
                            onEvent(SearchEvent.OnCategorySelected(category))
                        }
                )
                Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = stringResource(R.string.search_results),
                            style = MaterialTheme.typography.titleSmall
                    )
                    if (activeFeed is UiState.Success) {
                        val count = activeFeed.data.totalResults
                        Text(
                                text = pluralStringResource(R.plurals.search_matches, count, formatSearchCount(count)),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                key(uiState.query.trim(), uiState.category) {
                    SearchResultList(
                            feed = activeFeed,
                            movieGenreNames = uiState.movieGenreNames,
                            onRetry = { onEvent(SearchEvent.Retry) },
                            onItemClicked = { result ->
                                focusManager.clearFocus()
                                onItemClicked(result.id, result.mediaType)
                            },
                            modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(
        query: String,
        onQueryChanged: (String) -> Unit,
        onSearch: () -> Unit,
        modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val fieldLabel = stringResource(R.string.search_hint)

    BasicTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = modifier.fillMaxWidth().semantics { contentDescription = fieldLabel },
            singleLine = true,
            textStyle = MaterialTheme.typography.labelLarge.copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            decorationBox = { input ->
                Surface(
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, colors.outline),
                        color = colors.surface
                ) {
                    Row(
                            modifier = Modifier.heightIn(min = 48.dp).padding(start = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = colors.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                        )
                        Box(modifier = Modifier.weight(1f).padding(end = if (query.isEmpty()) 14.dp else 0.dp)) {
                            if (query.isEmpty()) {
                                Text(
                                        text = fieldLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = colors.onSurfaceVariant
                                )
                            }
                            input()
                        }
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChanged("") }) {
                                Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.search_clear),
                                        tint = colors.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
    )
}

@Composable
private fun SearchCategoryChips(uiState: SearchState, onSelected: (SearchCategory) -> Unit) {
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

private fun formatSearchCount(count: Int): String = NumberFormat.getIntegerInstance().format(count)

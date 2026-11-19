package com.aldiprahasta.tmdb.ui.favorite

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverMediaCard
import com.aldiprahasta.tmdb.ui.components.DiscoverOptionsSheet
import com.aldiprahasta.tmdb.ui.components.DiscoverPersonCard
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.SwipeBox
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteScreen(
        onItemClicked: (contentId: Int, mediaType: String) -> Unit,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: FavoriteViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val removedMessage = stringResource(R.string.favorite_removed)
    val undoLabel = stringResource(R.string.favorite_undo)
    val writeError = stringResource(R.string.favorite_write_error)

    LaunchedEffect(viewModel, removedMessage, undoLabel, writeError) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FavoriteEffect.Removed -> {
                    val result = snackbarHostState.showSnackbar(
                            message = removedMessage,
                            actionLabel = undoLabel,
                            duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(FavoriteEvent.UndoRemove(effect.favorite))
                    }
                }

                FavoriteEffect.WriteFailed -> snackbarHostState.showSnackbar(writeError)
            }
        }
    }

    DiscoverSystemBars()
    DiscoverTheme {
        FavoriteContent(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onEvent = viewModel::onEvent,
                onItemClicked = onItemClicked,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteContent(
        uiState: FavoriteState,
        snackbarHostState: SnackbarHostState,
        onEvent: (FavoriteEvent) -> Unit,
        onItemClicked: (Int, String) -> Unit,
        modifier: Modifier = Modifier
) {
    var showSortSheet by rememberSaveable { mutableStateOf(false) }
    val favorites = uiState.favorites

    Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                        title = {
                            Column {
                                Text(
                                        text = stringResource(R.string.dashboard_tab_favorites),
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                                if (favorites is UiState.Success) {
                                    val count = favorites.data.size
                                    Text(
                                            text = pluralStringResource(R.plurals.favorite_count, count, count),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        actions = {
                            FavoriteSortButton(
                                    order = uiState.sortOrder,
                                    onClick = { showSortSheet = true }
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                        )
                )
            }
    ) { innerPadding ->
        val contentModifier = Modifier.fillMaxSize().padding(innerPadding)

        when (favorites) {
            UiState.Loading -> LoadingScreen(contentModifier)
            is UiState.Error -> ErrorScreen(
                    modifier = contentModifier,
                    errorMessage = stringResource(R.string.favorite_load_error)
            )

            is UiState.Success -> if (favorites.data.isEmpty()) {
                Box(modifier = contentModifier.padding(18.dp), contentAlignment = Alignment.Center) {
                    Text(
                            text = stringResource(R.string.favorite_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                key(uiState.sortOrder) {
                    LazyColumn(
                            modifier = contentModifier,
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(favorites.data, key = { "${it.mediaType}:${it.favoriteId}" }) { favorite ->
                            val remove = { onEvent(FavoriteEvent.Remove(favorite)) }
                            SwipeBox(
                                    modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                                    onDelete = remove
                            ) {
                                FavoriteCard(
                                        favorite = favorite,
                                        onRemove = remove,
                                        onClick = { onItemClicked(favorite.favoriteId, favorite.mediaType) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSortSheet) {
        DiscoverOptionsSheet(
                title = stringResource(R.string.favorite_sort),
                description = stringResource(R.string.favorite_sort_description),
                applyLabel = stringResource(R.string.genre_apply_sort),
                options = FavoriteSortOrder.entries,
                appliedOption = uiState.sortOrder,
                optionLabel = { stringResource(it.label) },
                onDismiss = { showSortSheet = false },
                onApply = {
                    showSortSheet = false
                    onEvent(FavoriteEvent.ApplySort(it))
                }
        )
    }
}

@Composable
private fun FavoriteSortButton(order: FavoriteSortOrder, onClick: () -> Unit) {
    OutlinedButton(
            onClick = onClick,
            modifier = Modifier.padding(end = 18.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
                imageVector = Icons.Filled.SwapVert,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
        )
        Text(
                text = stringResource(order.label),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FavoriteCard(favorite: FavoriteDomainModel, onRemove: () -> Unit, onClick: () -> Unit) {
    val removeDescription = stringResource(R.string.favorite_remove_action, favorite.title)
    val heartColor = Color(0xFFDC4561)

    if (favorite.mediaType == MediaType.PERSON_TYPE.name) {
        val person = stringResource(R.string.search_type_person)
        DiscoverPersonCard(
                name = favorite.title,
                subtitle = favorite.knownFor?.let { stringResource(R.string.search_result_subtitle, person, it) } ?: person,
                profilePath = favorite.imagePoster,
                onClick = onClick,
                trailingContent = {
                    FilledIconButton(
                            onClick = onRemove,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = heartColor
                            )
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = removeDescription, modifier = Modifier.size(18.dp))
                    }
                }
        )
    } else {
        val isTv = favorite.mediaType == MediaType.TV_TYPE.name
        val releaseYear = favorite.releaseDate.takeLast(4).takeIf { it.length == 4 && it.all(Char::isDigit) }
        val metadata = if (isTv) {
            favorite.seasonCount?.let { pluralStringResource(R.plurals.favorite_seasons, it, it) }
        } else {
            favorite.genres?.substringBefore(',')?.takeIf { it.isNotBlank() }
        }
        DiscoverMediaCard(
                title = favorite.title,
                subtitle = listOfNotNull(releaseYear, metadata).joinToString(" · "),
                posterPath = favorite.imagePoster,
                voteAverage = favorite.voteAverage,
                description = if (isTv) stringResource(R.string.favorite_tv_series)
                else addedDateLabel(favorite.addedAt),
                onClick = onClick,
                trailingContent = {
                    IconButton(onClick = onRemove) {
                        Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = removeDescription,
                                tint = heartColor,
                                modifier = Modifier.size(18.dp)
                        )
                    }
                }
        )
    }
}

@Composable
private fun addedDateLabel(timestamp: Long): String {
    val savedDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    val today = Calendar.getInstance()
    val yesterday = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        savedDate.isSameDay(today) -> stringResource(R.string.favorite_added_today)
        savedDate.isSameDay(yesterday) -> stringResource(R.string.favorite_added_yesterday)
        else -> stringResource(
                R.string.favorite_added_date,
                DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(timestamp))
        )
    }
}

private fun Calendar.isSameDay(other: Calendar): Boolean =
        get(Calendar.YEAR) == other.get(Calendar.YEAR) && get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)

@Preview(showBackground = true)
@Composable
private fun FavoriteContentPreview() {
    DiscoverTheme {
        FavoriteContent(
                uiState = FavoriteState(favorites = UiState.Success(listOf(
                        FavoriteDomainModel(
                                favoriteId = 1,
                                title = "Dune: Part Two",
                                releaseDate = "March 1, 2024",
                                imagePoster = null,
                                mediaType = MediaType.MOVIE_TYPE.name,
                                addedAt = System.currentTimeMillis(),
                                voteAverage = 8.8,
                                genres = "Science Fiction"
                        )
                ))),
                snackbarHostState = remember { SnackbarHostState() },
                onEvent = {},
                onItemClicked = { _, _ -> }
        )
    }
}

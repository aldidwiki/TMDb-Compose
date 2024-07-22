package com.aldiprahasta.tmdb.ui.favorite

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.ui.components.ContentItem
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.SwipeBox
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteScreen(
        onItemClicked: (contentId: Int, mediaType: String) -> Unit,
        modifier: Modifier = Modifier,
) {
    val viewModel: FavoriteViewModel = koinViewModel()
    val favoritesState by viewModel.favorites.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        AnimatedContent(
                targetState = favoritesState,
                label = "Animated Favorite",
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(tween(500))
                }
        ) { targetState ->
            targetState.apply {
                doIfLoading {
                    LoadingScreen()
                }

                doIfError { _, errorMessage ->
                    ErrorScreen(errorMessage = errorMessage)
                }

                doIfSuccess { favorites ->
                    FavoriteContent(
                            favorites = favorites,
                            onItemClicked = onItemClicked,
                            onSwipeDelete = {
                                viewModel.deleteFavorite(it)
                            }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FavoriteContent(
        favorites: List<FavoriteDomainModel>,
        onItemClicked: (contentId: Int, mediaType: String) -> Unit,
        onSwipeDelete: (contentId: Int) -> Unit,
        modifier: Modifier = Modifier
) {
    LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(favorites) { index, favorite ->
            SwipeBox(onDelete = {
                onSwipeDelete(favorite.favoriteId)
            }) {
                ContentItem(
                        title = favorite.title,
                        releaseDate = favorite.releaseDate,
                        characterName = null,
                        posterPath = favorite.imagePoster,
                        totalEpisodeCount = null,
                        modifier = Modifier
                                .animateItemPlacement()
                                .background(MaterialTheme.colorScheme.background),
                        onItemClicked = {
                            onItemClicked(favorite.favoriteId, favorite.mediaType)
                        }
                )
            }

            if (index < favorites.size - 1) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteContentPreview(modifier: Modifier = Modifier) {
    FavoriteContent(
            listOf(
                    FavoriteDomainModel(
                            favoriteId = 5278,
                            title = "prodesset",
                            releaseDate = "magna",
                            imagePoster = null,
                            mediaType = "instructior"
                    ),
                    FavoriteDomainModel(
                            favoriteId = 5278,
                            title = "prodesset",
                            releaseDate = "magna",
                            imagePoster = null,
                            mediaType = "instructior"
                    ),
                    FavoriteDomainModel(
                            favoriteId = 5278,
                            title = "prodesset",
                            releaseDate = "magna",
                            imagePoster = null,
                            mediaType = "instructior"
                    ),
                    FavoriteDomainModel(
                            favoriteId = 5278,
                            title = "prodesset",
                            releaseDate = "magna",
                            imagePoster = null,
                            mediaType = "instructior"
                    ),
            ),
            onItemClicked = { _, _ -> },
            onSwipeDelete = {}
    )
}
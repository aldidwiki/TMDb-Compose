package com.aldiprahasta.tmdb.ui.favorite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.ui.components.SwipeBox
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview

private const val FavoriteFadeDuration = 180

@Composable
internal fun FavoriteList(
        favorites: List<FavoriteDomainModel>,
        onRemove: (FavoriteDomainModel) -> Unit,
        onItemClicked: (Int, String) -> Unit,
        modifier: Modifier = Modifier
) {
    Box(modifier) {
        LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favorites, key = { "${it.mediaType}:${it.favoriteId}" }) { favorite ->
                val remove = { onRemove(favorite) }
                SwipeBox(
                        modifier = Modifier.animateItem(
                                fadeInSpec = tween(FavoriteFadeDuration),
                                placementSpec = tween(durationMillis = 280),
                                fadeOutSpec = tween(FavoriteFadeDuration)
                        ).clip(RoundedCornerShape(16.dp)),
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
        AnimatedVisibility(
                visible = favorites.isEmpty(),
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 18.dp),
                enter = fadeIn(tween(durationMillis = 150, delayMillis = FavoriteFadeDuration)),
                exit = fadeOut(tween(durationMillis = 100))
        ) {
            Text(
                    text = stringResource(R.string.favorite_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(widthDp = 390, heightDp = 300)
@Composable
private fun FavoriteListPreview() {
    DiscoverPreview {
        FavoriteList(
                favorites = listOf(favoritePreviewItem, favoritePreviewItem.copy(favoriteId = 2, title = "Arrival")),
                onRemove = {},
                onItemClicked = { _, _ -> }
        )
    }
}

package com.aldiprahasta.tmdb.ui.favorite

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.utils.UiState

@Immutable
data class FavoriteState(
        val favorites: UiState<List<FavoriteDomainModel>> = UiState.Loading,
        val sortOrder: FavoriteSortOrder = FavoriteSortOrder.RECENTLY_ADDED
)

enum class FavoriteSortOrder(@param:StringRes val label: Int) {
    RECENTLY_ADDED(R.string.favorite_sort_recently_added),
    OLDEST_ADDED(R.string.favorite_sort_oldest_added),
    HIGHEST_RATED(R.string.genre_sort_highest_rated),
    TITLE(R.string.genre_sort_title),
    MOST_VOTED(R.string.genre_sort_most_voted)
}

package com.aldiprahasta.tmdb.ui.favorite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.FavoriteWrapper
import com.aldiprahasta.tmdb.utils.mapDomainModelToEntity
import com.aldiprahasta.tmdb.utils.mapSuccess
import java.text.Collator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoriteViewModel(
        private val favoriteWrapper: FavoriteWrapper,
        private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val sortOrder = savedStateHandle.getStateFlow(SORT_ORDER_KEY, FavoriteSortOrder.RECENTLY_ADDED)
    private val effectChannel = Channel<FavoriteEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    val uiState = combine(favoriteWrapper.getAllFavoriteList(), sortOrder) { favorites, order ->
        FavoriteState(
                favorites = favorites.mapSuccess { items -> items.sortedWith(order.comparator()) },
                sortOrder = order
        )
    }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            FavoriteState(sortOrder = sortOrder.value)
    )

    fun onEvent(event: FavoriteEvent) {
        when (event) {
            is FavoriteEvent.ApplySort -> savedStateHandle[SORT_ORDER_KEY] = event.sortOrder

            is FavoriteEvent.Remove -> updateFavorites {
                val favorite = event.favorite
                if (favoriteWrapper.deleteFavorite(favorite.favoriteId, favorite.mediaType)) {
                    effectChannel.send(FavoriteEffect.Removed(favorite))
                }
            }

            is FavoriteEvent.UndoRemove -> updateFavorites {
                favoriteWrapper.insertFavorite(event.favorite.mapDomainModelToEntity())
            }
        }
    }

    private fun updateFavorites(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Timber.e(exception)
                effectChannel.send(FavoriteEffect.WriteFailed)
            }
        }
    }

    private fun FavoriteSortOrder.comparator(): Comparator<FavoriteDomainModel> {
        val byTitle = compareBy<FavoriteDomainModel, String>(Collator.getInstance()) { it.title }
                .thenBy { it.mediaType }
                .thenBy { it.favoriteId }

        return when (this) {
            FavoriteSortOrder.RECENTLY_ADDED -> compareByDescending<FavoriteDomainModel> { it.addedAt }.then(byTitle)
            FavoriteSortOrder.OLDEST_ADDED -> compareBy<FavoriteDomainModel> { it.addedAt }.then(byTitle)
            FavoriteSortOrder.HIGHEST_RATED -> compareByDescending<FavoriteDomainModel> { it.voteAverage }.then(byTitle)
            FavoriteSortOrder.TITLE -> byTitle
            FavoriteSortOrder.MOST_VOTED -> compareByDescending<FavoriteDomainModel> { it.voteCount }.then(byTitle)
        }
    }

    private companion object {
        const val SORT_ORDER_KEY = "favorite_sort_order"
    }
}

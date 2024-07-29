package com.aldiprahasta.tmdb.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.FavoriteWrapper
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.toStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteWrapper: FavoriteWrapper) : ViewModel() {
    val favorites: StateFlow<UiState<List<FavoriteDomainModel>>> = favoriteWrapper.getAllFavoriteList()
            .toStateFlow(viewModelScope)

    fun deleteFavorite(id: Int) {
        viewModelScope.launch {
            favoriteWrapper.deleteFavorite(id)
        }
    }
}
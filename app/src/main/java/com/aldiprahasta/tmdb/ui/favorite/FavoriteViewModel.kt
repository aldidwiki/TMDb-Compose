package com.aldiprahasta.tmdb.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.domain.usecase.favorite.GetAllFavoriteList
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.delayAfterLoading
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoriteViewModel(getAllFavoriteList: GetAllFavoriteList) : ViewModel() {
    val favorites: StateFlow<UiState<List<FavoriteDomainModel>>> = getAllFavoriteList()
            .delayAfterLoading(300L)
            .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    UiState.Loading
            )
}
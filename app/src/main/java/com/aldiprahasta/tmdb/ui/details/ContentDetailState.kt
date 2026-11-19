package com.aldiprahasta.tmdb.ui.details

import androidx.compose.runtime.Immutable
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.SimilarPage
import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.utils.UiState

@Immutable
data class ContentDetailState(
        val detail: UiState<ContentDetailDomainModel> = UiState.Loading,
        val similar: UiState<SimilarPage> = UiState.Loading,
        val selectedSeason: Int? = null,
        val seasons: Map<Int, UiState<TvSeasonDetailDomainModel>> = emptyMap(),
        val isFavorite: Boolean = false,
        val isFavoriteAvailable: Boolean = false,
        val isSavingFavorite: Boolean = false
)

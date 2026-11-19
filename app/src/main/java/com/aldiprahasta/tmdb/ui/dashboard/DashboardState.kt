package com.aldiprahasta.tmdb.ui.dashboard

import androidx.compose.runtime.Immutable
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.model.VideoDomainModel
import com.aldiprahasta.tmdb.utils.UiState

@Immutable
data class DashboardState(
        val trendingMovies: UiState<List<MovieDomainModel>> = UiState.Loading,
        val nowPlayingMovies: UiState<List<MovieDomainModel>> = UiState.Loading,
        val genres: UiState<List<GenreDomainModel>> = UiState.Loading,
        val trailers: UiState<List<VideoDomainModel>>? = null
)

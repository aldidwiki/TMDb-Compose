package com.aldiprahasta.tmdb.ui.dashboard

sealed interface DashboardEvent {
    data object RetryTrending : DashboardEvent
    data object RetryNowPlaying : DashboardEvent
    data object RetryGenres : DashboardEvent
    data class OnWatchTrailerClicked(val movieId: Int) : DashboardEvent
    data object DismissTrailers : DashboardEvent
}

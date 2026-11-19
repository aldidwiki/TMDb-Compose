package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ImageResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

class MovieRemoteDataSource(private val remoteService: RemoteService) {
    fun getTrendingMovies(): Flow<UiState<MovieResponse>> = remoteRequest {
        remoteService.getTrendingMovies()
    }

    fun getNowPlayingMovies(region: String): Flow<UiState<MovieResponse>> = remoteRequest {
        remoteService.getNowPlayingMovies(region)
    }

    fun getMovieVideos(movieId: Int): Flow<UiState<VideoResponse>> = remoteRequest {
        remoteService.getMovieVideos(movieId)
    }

    fun getMovieDetail(movieId: Int): Flow<UiState<MovieDetailResponse>> = remoteRequest {
        remoteService.getMovieDetail(movieId)
    }

    fun getMovieCredits(movieId: Int): Flow<UiState<CreditResponse>> = remoteRequest {
        remoteService.getMovieCredits(movieId)
    }

    fun getMovieGenres(): Flow<UiState<GenreResponse>> = remoteRequest {
        remoteService.getMovieGenres()
    }

    fun getMovieImages(movieId: Int): Flow<UiState<ImageResponse>> = remoteRequest {
        remoteService.getMovieImages(movieId)
    }
}

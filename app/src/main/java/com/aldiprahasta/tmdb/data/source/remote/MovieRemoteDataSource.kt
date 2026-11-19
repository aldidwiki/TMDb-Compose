package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ImageResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class MovieRemoteDataSource(private val remoteService: RemoteService) {
    fun getTrendingMovies(): Flow<UiState<MovieResponse>> = request {
        remoteService.getTrendingMovies()
    }

    fun getNowPlayingMovies(region: String): Flow<UiState<MovieResponse>> = request {
        remoteService.getNowPlayingMovies(region)
    }

    fun getMovieVideos(movieId: Int): Flow<UiState<VideoResponse>> = request {
        remoteService.getMovieVideos(movieId)
    }

    fun getMovieDetail(movieId: Int): Flow<UiState<MovieDetailResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getMovieDetail(movieId)
        response?.let { movieDetailResponse ->
            emit(UiState.Success(movieDetailResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)

    fun getMovieCredits(movieId: Int): Flow<UiState<CreditResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getMovieCredits(movieId)
        response?.let { creditResponse ->
            emit(UiState.Success(creditResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)

    fun getMovieGenres(): Flow<UiState<GenreResponse>> = request {
        remoteService.getMovieGenres()
    }

    fun getMovieImages(movieId: Int): Flow<UiState<ImageResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getMovieImages(movieId)
        response?.let { imageResponse ->
            emit(UiState.Success(imageResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)

    private fun <T : Any> request(call: suspend () -> T?): Flow<UiState<T>> = flow {
        emit(UiState.Loading)
        emit(UiState.Success(checkNotNull(call()) { "TMDb returned an empty response" }))
    }.catch { throwable ->
        Timber.e(throwable)
        emit(UiState.Error(throwable))
    }.flowOn(Dispatchers.IO)
}

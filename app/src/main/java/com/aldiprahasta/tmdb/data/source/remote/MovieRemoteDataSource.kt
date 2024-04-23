package com.aldiprahasta.tmdb.data.source.remote

import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRemoteDataSource @Inject constructor(private val remoteService: RemoteService) {
    fun getPopularMovies(): Flow<UiState<MovieResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getPopularMovies()
        if (!response.isSuccessful) throw HttpException(response)
        else response.body()?.let { movieResponse ->
            emit(UiState.Success(movieResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)

    fun getMovieDetail(movieId: Int): Flow<UiState<MovieDetailResponse>> = flow {
        emit(UiState.Loading)
        val response = remoteService.getMovieDetail(movieId)
        if (!response.isSuccessful) throw HttpException(response)
        else response.body()?.let { movieDetailResponse ->
            emit(UiState.Success(movieDetailResponse))
        }
    }.catch { t ->
        Timber.e(t)
        emit(UiState.Error(t))
    }.flowOn(Dispatchers.IO)
}
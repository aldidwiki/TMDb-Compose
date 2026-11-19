package com.aldiprahasta.tmdb.domain.repository

import androidx.paging.PagingData
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ImageResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponseModel
import com.aldiprahasta.tmdb.domain.model.GenreMovieQuery
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getPopularMovie(): Flow<PagingData<MovieResponseModel>>
    fun getTrendingMovies(): Flow<UiState<MovieResponse>>
    fun getNowPlayingMovies(region: String): Flow<UiState<MovieResponse>>
    fun getMoviesByGenre(query: GenreMovieQuery): Flow<PagingData<MovieResponseModel>>
    fun getMovieVideos(movieId: Int): Flow<UiState<VideoResponse>>
    fun getMovieDetail(movieId: Int): Flow<UiState<MovieDetailResponse>>
    fun getMovieCredits(movieId: Int): Flow<UiState<CreditResponse>>
    fun getMovieGenres(): Flow<UiState<GenreResponse>>
    fun getMovieImages(movieId: Int): Flow<UiState<ImageResponse>>
}

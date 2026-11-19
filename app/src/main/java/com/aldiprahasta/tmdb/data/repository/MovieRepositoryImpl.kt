package com.aldiprahasta.tmdb.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aldiprahasta.tmdb.data.source.paging.GenreMoviePagingSource
import com.aldiprahasta.tmdb.data.source.paging.PopularMoviePagingSource
import com.aldiprahasta.tmdb.data.source.remote.MovieRemoteDataSource
import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ImageResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponseModel
import com.aldiprahasta.tmdb.domain.model.GenreMovieQuery
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

class MovieRepositoryImpl(
        private val movieRemoteDataSource: MovieRemoteDataSource,
        private val remoteService: RemoteService
) : MovieRepository {
    override fun getPopularMovie(): Flow<PagingData<MovieResponseModel>> {
        return Pager(
                config = PagingConfig(pageSize = 10),
                pagingSourceFactory = {
                    PopularMoviePagingSource(remoteService)
                }
        ).flow
    }

    override fun getMovieDetail(movieId: Int): Flow<UiState<MovieDetailResponse>> {
        return movieRemoteDataSource.getMovieDetail(movieId)
    }

    override fun getTrendingMovies(): Flow<UiState<MovieResponse>> {
        return movieRemoteDataSource.getTrendingMovies()
    }

    override fun getNowPlayingMovies(region: String): Flow<UiState<MovieResponse>> {
        return movieRemoteDataSource.getNowPlayingMovies(region)
    }

    override fun getMoviesByGenre(query: GenreMovieQuery): Flow<PagingData<MovieResponseModel>> {
        return Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = { GenreMoviePagingSource(remoteService, query) }
        ).flow
    }

    override fun getMovieVideos(movieId: Int): Flow<UiState<VideoResponse>> {
        return movieRemoteDataSource.getMovieVideos(movieId)
    }

    override fun getMovieCredits(movieId: Int): Flow<UiState<CreditResponse>> {
        return movieRemoteDataSource.getMovieCredits(movieId)
    }

    override fun getMovieGenres(): Flow<UiState<GenreResponse>> {
        return movieRemoteDataSource.getMovieGenres()
    }

    override fun getMovieImages(movieId: Int): Flow<UiState<ImageResponse>> {
        return movieRemoteDataSource.getMovieImages(movieId)
    }
}

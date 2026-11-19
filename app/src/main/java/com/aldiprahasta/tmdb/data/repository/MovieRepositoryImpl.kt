package com.aldiprahasta.tmdb.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aldiprahasta.tmdb.data.source.paging.NumberedPagingSource
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
import com.aldiprahasta.tmdb.domain.model.MovieSortOrder
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
                    NumberedPagingSource { page ->
                        val response = remoteService.getPopularMovies(page)
                        NumberedPagingSource.Page(response?.movieResponseModelList.orEmpty(), response?.totalPages ?: 0)
                    }
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
        val sort = when (query.sortOrder) {
            MovieSortOrder.POPULARITY -> "popularity.desc"
            MovieSortOrder.NEWEST -> "primary_release_date.desc"
            MovieSortOrder.HIGHEST_RATED -> "vote_average.desc"
            MovieSortOrder.TITLE -> "title.asc"
            MovieSortOrder.MOST_VOTED -> "vote_count.desc"
        }
        return Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = {
                    NumberedPagingSource { page ->
                        val response = remoteService.getMoviesByGenre(
                                genreId = query.genreId,
                                sort = sort,
                                page = page,
                                releaseYear = query.releaseYear,
                                minimumRating = query.minimumRating
                        )
                        NumberedPagingSource.Page(response.movieResponseModelList, response.totalPages)
                    }
                }
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

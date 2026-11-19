package com.aldiprahasta.tmdb.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponseModel
import com.aldiprahasta.tmdb.domain.model.GenreMovieQuery
import com.aldiprahasta.tmdb.domain.model.MovieSortOrder
import kotlinx.coroutines.CancellationException
import timber.log.Timber

class GenreMoviePagingSource(
        private val remoteService: RemoteService,
        private val query: GenreMovieQuery
) : PagingSource<Int, MovieResponseModel>() {
    private val sort = when (query.sortOrder) {
        MovieSortOrder.POPULARITY -> "popularity.desc"
        MovieSortOrder.NEWEST -> "primary_release_date.desc"
        MovieSortOrder.HIGHEST_RATED -> "vote_average.desc"
        MovieSortOrder.TITLE -> "title.asc"
        MovieSortOrder.MOST_VOTED -> "vote_count.desc"
    }

    override fun getRefreshKey(state: PagingState<Int, MovieResponseModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieResponseModel> {
        return try {
            val page = params.key ?: 1
            val response = remoteService.getMoviesByGenre(
                    genreId = query.genreId,
                    sort = sort,
                    page = page,
                    releaseYear = query.releaseYear,
                    minimumRating = query.minimumRating
            )

            LoadResult.Page(
                    data = response.movieResponseModelList,
                    prevKey = (page - 1).takeIf { page > 1 },
                    nextKey = (page + 1).takeIf {
                        page < minOf(response.totalPages, MAX_PAGE) && response.movieResponseModelList.isNotEmpty()
                    }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e)
            LoadResult.Error(e)
        }
    }

    private companion object {
        const val MAX_PAGE = 500
    }
}

package com.aldiprahasta.tmdb.data.source.paging

import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponseModel
import retrofit2.HttpException
import timber.log.Timber

class MoviePagingSource(
        private val remoteService: RemoteService
) : PagingSource<Int, MovieResponseModel>() {
    override fun getRefreshKey(state: PagingState<Int, MovieResponseModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieResponseModel> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val response = remoteService.getPopularMovies(page)

            if (!response.isSuccessful) {
                LoadState.Error(HttpException(response))
            }

            LoadResult.Page(
                    data = response.body()?.movieResponseModelList ?: emptyList(),
                    prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                    nextKey = if (response.body()?.movieResponseModelList.isNullOrEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Timber.e(e)
            return LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}
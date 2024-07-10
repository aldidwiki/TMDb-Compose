package com.aldiprahasta.tmdb.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.search.SearchResponseModel
import retrofit2.HttpException
import timber.log.Timber

class SearchPagingSource(
        private val remoteService: RemoteService,
        private val query: String
) : PagingSource<Int, SearchResponseModel>() {
    override fun getRefreshKey(state: PagingState<Int, SearchResponseModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResponseModel> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val response = remoteService.getSearchResults(query, page)

            if (!response.isSuccessful) {
                throw HttpException(response)
            }

            LoadResult.Page(
                    data = response.body()?.results ?: emptyList(),
                    prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                    nextKey = if (response.body()?.results.isNullOrEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Timber.e(e)
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}
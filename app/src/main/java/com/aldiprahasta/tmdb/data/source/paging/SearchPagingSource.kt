package com.aldiprahasta.tmdb.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.domain.model.SearchPage
import kotlinx.coroutines.CancellationException
import timber.log.Timber

class SearchPagingSource(
        private val firstPage: SearchPage?,
        private val loadPage: suspend (Int) -> SearchPage
) : PagingSource<Int, SearchDomainModel>() {
    override fun getRefreshKey(state: PagingState<Int, SearchDomainModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchDomainModel> {
        return try {
            val page = params.key ?: 1
            val response = if (page == 1 && firstPage != null) firstPage else loadPage(page)

            LoadResult.Page(
                    data = response.results,
                    prevKey = (page - 1).takeIf { page > 1 },
                    nextKey = (page + 1).takeIf {
                        page < minOf(response.totalPages, MAX_PAGE) && response.results.isNotEmpty()
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

package com.aldiprahasta.tmdb.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import timber.log.Timber

internal class NumberedPagingSource<T : Any>(
        private val loadPage: suspend (Int) -> Page<T>
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        val page = state.anchorPosition?.let(state::closestPageToPosition)
        return page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 1
            val response = loadPage(page)
            LoadResult.Page(
                    data = response.items,
                    prevKey = (page - 1).takeIf { page > 1 },
                    nextKey = (page + 1).takeIf {
                        page < minOf(response.totalPages, MAX_PAGE) && response.items.isNotEmpty()
                    }
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Timber.e(error)
            LoadResult.Error(error)
        }
    }

    data class Page<T>(val items: List<T>, val totalPages: Int)

    private companion object {
        const val MAX_PAGE = 500
    }
}

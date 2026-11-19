package com.aldiprahasta.tmdb.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aldiprahasta.tmdb.domain.model.SimilarContent
import com.aldiprahasta.tmdb.domain.model.SimilarPage
import kotlinx.coroutines.CancellationException
import timber.log.Timber

internal class SimilarPagingSource(
        private val firstPage: SimilarPage,
        private val getPage: suspend (Int) -> SimilarPage
) : PagingSource<Int, SimilarContent>() {
    override fun getRefreshKey(state: PagingState<Int, SimilarContent>): Int? = state.anchorPosition?.let { index ->
        val page = state.closestPageToPosition(index)
        page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimilarContent> {
        return try {
            val page = params.key ?: 1
            val response = if (page == 1) firstPage else getPage(page)
            LoadResult.Page(
                    data = response.items,
                    prevKey = (page - 1).takeIf { page > 1 },
                    nextKey = (page + 1).takeIf { page < minOf(response.totalPages, 500) && response.items.isNotEmpty() }
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Timber.e(error)
            LoadResult.Error(error)
        }
    }
}

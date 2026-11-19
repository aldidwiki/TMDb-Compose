package com.aldiprahasta.tmdb.data.source.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class NumberedPagingSourceTest {
    @Test
    fun defaultPageAndRefreshKeyFollowTmdbPageNumbers() = runBlocking {
        val source = NumberedPagingSource { page -> NumberedPagingSource.Page(listOf(page), totalPages = 3) }
        val first = source.load(PagingSource.LoadParams.Refresh(null, 60, false))
        val second = source.load(PagingSource.LoadParams.Append(2, 20, false))

        assertEquals(PagingSource.LoadResult.Page(listOf(1), null, 2), first)
        assertEquals(PagingSource.LoadResult.Page(listOf(2), 1, 3), second)
        assertEquals(2, source.getRefreshKey(PagingState(
                pages = listOf(first as PagingSource.LoadResult.Page, second as PagingSource.LoadResult.Page),
                anchorPosition = 1,
                config = PagingConfig(pageSize = 20),
                leadingPlaceholderCount = 0
        )))
    }

    @Test
    fun stopsAtTheApiLimitAndAtAnEmptyPage() = runBlocking {
        val source = NumberedPagingSource { page ->
            val items = if (page == 2) emptyList() else listOf(page)
            NumberedPagingSource.Page(items, totalPages = 700)
        }

        assertEquals(PagingSource.LoadResult.Page(listOf(500), 499, null),
                source.load(PagingSource.LoadParams.Append(500, 20, false)))
        assertEquals(PagingSource.LoadResult.Page<Int, Int>(emptyList(), 1, null),
                source.load(PagingSource.LoadParams.Append(2, 20, false)))
    }

    @Test
    fun returnsRetryableErrorsAndPropagatesCancellation() {
        val failure = IllegalStateException("Request failed")
        val source = NumberedPagingSource<Int> { throw failure }
        val params = PagingSource.LoadParams.Refresh<Int>(null, 20, false)
        val result = runBlocking { source.load(params) }

        assertSame(failure, (result as PagingSource.LoadResult.Error).throwable)

        val cancelled = NumberedPagingSource<Int> { throw CancellationException() }
        assertThrows(CancellationException::class.java) {
            runBlocking { cancelled.load(params) }
        }
    }
}

package com.aldiprahasta.tmdb.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aldiprahasta.tmdb.domain.model.ExploreItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal data class ExplorePage(val items: List<ExploreItem>, val nextPage: Int?)
internal typealias ExploreFeed = suspend (page: Int) -> ExplorePage

internal class ExplorePagingSource(
        private val feeds: List<ExploreFeed>,
        private val order: Comparator<ExploreItem>?
) : PagingSource<Int, ExploreItem>() {
    private var positions = feeds.map { FeedPosition() }
    private var nextFeed = 0
    private val emitted = mutableSetOf<String>()

    override fun getRefreshKey(state: PagingState<Int, ExploreItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ExploreItem> = try {
        val loaded = ArrayList<ExploreItem>(params.loadSize)
        val newKeys = mutableSetOf<String>()
        val progress = positions.toMutableList()
        var rotation = nextFeed

        while (loaded.size < params.loadSize) {
            val pending = progress.indices.filter { index ->
                val position = progress[index]
                position.remaining.isEmpty() && position.nextPage != null
            }
            if (pending.isNotEmpty()) {
                val updates = coroutineScope {
                    pending.map { index ->
                        async { index to advance(index, progress[index]) }
                    }.awaitAll()
                }
                updates.forEach { (index, position) -> progress[index] = position }
            }

            val available = progress.indices.filter { progress[it].remaining.isNotEmpty() }
            if (available.isEmpty()) break
            val index = if (order != null) {
                available.minWith { first, second ->
                    order.compare(progress[first].remaining.first(), progress[second].remaining.first())
                }
            } else {
                available.firstOrNull { it >= rotation } ?: available.first()
            }

            val position = progress[index]
            val item = position.remaining.first()
            progress[index] = position.copy(remaining = position.remaining.drop(1))
            if (order == null) rotation = (index + 1) % feeds.size
            if (item.key !in emitted && newKeys.add(item.key)) loaded.add(item)
        }

        positions = progress
        nextFeed = rotation
        emitted.addAll(newKeys)
        LoadResult.Page(
                data = loaded,
                prevKey = null,
                nextKey = ((params.key ?: 0) + 1).takeIf { progress.any { it.remaining.isNotEmpty() || it.nextPage != null } }
        )
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        LoadResult.Error(error)
    }

    private suspend fun advance(index: Int, from: FeedPosition): FeedPosition {
        var page = from.nextPage
        while (page != null) {
            val response = feeds[index](page)
            if (response.items.isNotEmpty()) return FeedPosition(response.nextPage, response.items)
            page = response.nextPage
        }
        return FeedPosition(nextPage = null)
    }

    private data class FeedPosition(
            val nextPage: Int? = 1,
            val remaining: List<ExploreItem> = emptyList()
    )
}

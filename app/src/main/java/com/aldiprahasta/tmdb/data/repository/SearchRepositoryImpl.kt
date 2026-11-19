package com.aldiprahasta.tmdb.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aldiprahasta.tmdb.data.source.paging.NumberedPagingSource
import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.domain.model.SearchCategory
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.domain.model.SearchPage
import com.aldiprahasta.tmdb.domain.repository.SearchRepository
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.mapSearchResponseToDomainModel
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.atomic.AtomicReference

class SearchRepositoryImpl(private val remoteService: RemoteService) : SearchRepository {
    override suspend fun getFirstPage(query: String, category: SearchCategory): SearchPage =
            getPage(query, category, page = 1)

    override fun getSearchResults(
            query: String,
            category: SearchCategory,
            firstPage: SearchPage
    ): Flow<PagingData<SearchDomainModel>> {
        val initialPage = AtomicReference<SearchPage?>(firstPage)
        return Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = {
                    val cachedPage = initialPage.getAndSet(null)
                    NumberedPagingSource { page ->
                        val response = if (page == 1 && cachedPage != null) cachedPage
                        else getPage(query, category, page)
                        NumberedPagingSource.Page(response.results, response.totalPages)
                    }
                }
        ).flow
    }

    private suspend fun getPage(query: String, category: SearchCategory, page: Int): SearchPage {
        val response = when (category) {
            SearchCategory.ALL -> remoteService.getSearchResults(query, page)
            SearchCategory.MOVIES -> remoteService.searchMovies(query, page)
            SearchCategory.TV -> remoteService.searchTv(query, page)
            SearchCategory.PEOPLE -> remoteService.searchPeople(query, page)
        }
        val categoryMediaType = when (category) {
            SearchCategory.ALL -> null
            SearchCategory.MOVIES -> Constant.MOVIE_MEDIA_TYPE
            SearchCategory.TV -> Constant.TV_MEDIA_TYPE
            SearchCategory.PEOPLE -> Constant.PERSON_MEDIA_TYPE
        }
        return SearchPage(
                results = response.results.map { it.mapSearchResponseToDomainModel(categoryMediaType) },
                totalPages = response.totalPages,
                totalResults = response.totalResults
        )
    }
}

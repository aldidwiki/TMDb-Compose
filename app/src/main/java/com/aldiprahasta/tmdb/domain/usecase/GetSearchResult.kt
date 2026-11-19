package com.aldiprahasta.tmdb.domain.usecase

import androidx.paging.PagingData
import com.aldiprahasta.tmdb.domain.model.SearchCategory
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.domain.model.SearchPage
import com.aldiprahasta.tmdb.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class GetSearchResult(private val searchRepository: SearchRepository) {
    suspend fun getFirstPage(query: String, category: SearchCategory): SearchPage =
            searchRepository.getFirstPage(query, category)

    operator fun invoke(
            query: String,
            category: SearchCategory,
            firstPage: SearchPage
    ): Flow<PagingData<SearchDomainModel>> = searchRepository.getSearchResults(query, category, firstPage)
}

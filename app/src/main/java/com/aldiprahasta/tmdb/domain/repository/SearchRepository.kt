package com.aldiprahasta.tmdb.domain.repository

import androidx.paging.PagingData
import com.aldiprahasta.tmdb.domain.model.SearchCategory
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.domain.model.SearchPage
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun getFirstPage(query: String, category: SearchCategory): SearchPage

    fun getSearchResults(
            query: String,
            category: SearchCategory,
            firstPage: SearchPage
    ): Flow<PagingData<SearchDomainModel>>
}

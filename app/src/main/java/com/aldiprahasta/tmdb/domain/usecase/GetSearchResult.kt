package com.aldiprahasta.tmdb.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.domain.repository.SearchRepository
import com.aldiprahasta.tmdb.utils.mapSearchResponseToDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSearchResult(private val searchRepository: SearchRepository) {
    operator fun invoke(query: String): Flow<PagingData<SearchDomainModel>> = searchRepository
            .getSearchResults(query).map { pagingData ->
                pagingData.map { searchResponseModel ->
                    searchResponseModel.mapSearchResponseToDomainModel()
                }
            }
}
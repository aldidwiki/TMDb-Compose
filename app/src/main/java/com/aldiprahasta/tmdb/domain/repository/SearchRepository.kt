package com.aldiprahasta.tmdb.domain.repository

import androidx.paging.PagingData
import com.aldiprahasta.tmdb.data.source.remote.response.search.SearchResponseModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun getSearchResults(query: String): Flow<PagingData<SearchResponseModel>>
}
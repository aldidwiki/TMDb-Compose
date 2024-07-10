package com.aldiprahasta.tmdb.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aldiprahasta.tmdb.data.source.paging.SearchPagingSource
import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.search.SearchResponseModel
import com.aldiprahasta.tmdb.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchRepositoryImpl(private val remoteService: RemoteService) : SearchRepository {
    override fun getSearchResults(query: String): Flow<PagingData<SearchResponseModel>> {
        return Pager(
                config = PagingConfig(pageSize = 10),
                pagingSourceFactory = {
                    SearchPagingSource(remoteService, query)
                }
        ).flow
    }
}
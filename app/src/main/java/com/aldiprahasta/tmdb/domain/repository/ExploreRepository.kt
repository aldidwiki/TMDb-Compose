package com.aldiprahasta.tmdb.domain.repository

import androidx.paging.PagingData
import com.aldiprahasta.tmdb.domain.model.ExploreItem
import com.aldiprahasta.tmdb.domain.model.ExploreQuery
import kotlinx.coroutines.flow.Flow

interface ExploreRepository {
    fun explore(query: ExploreQuery): Flow<PagingData<ExploreItem>>
}

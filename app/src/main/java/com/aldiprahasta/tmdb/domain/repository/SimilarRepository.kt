package com.aldiprahasta.tmdb.domain.repository

import androidx.paging.PagingData
import com.aldiprahasta.tmdb.domain.model.SimilarContent
import com.aldiprahasta.tmdb.domain.model.SimilarPage
import com.aldiprahasta.tmdb.utils.MediaType
import kotlinx.coroutines.flow.Flow

interface SimilarRepository {
    suspend fun getFirstPage(contentId: Int, mediaType: MediaType): SimilarPage
    fun getSimilarContent(contentId: Int, mediaType: MediaType, firstPage: SimilarPage): Flow<PagingData<SimilarContent>>
}

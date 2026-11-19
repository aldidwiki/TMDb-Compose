package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.SimilarPage
import com.aldiprahasta.tmdb.domain.repository.SimilarRepository
import com.aldiprahasta.tmdb.utils.MediaType

class GetSimilarContent(private val repository: SimilarRepository) {
    suspend fun getFirstPage(contentId: Int, mediaType: MediaType): SimilarPage = repository.getFirstPage(contentId, mediaType)

    operator fun invoke(contentId: Int, mediaType: MediaType, firstPage: SimilarPage) =
            repository.getSimilarContent(contentId, mediaType, firstPage)
}

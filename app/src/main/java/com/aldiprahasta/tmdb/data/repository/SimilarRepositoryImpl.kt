package com.aldiprahasta.tmdb.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.aldiprahasta.tmdb.data.source.paging.SimilarPagingSource
import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.domain.model.SimilarPage
import com.aldiprahasta.tmdb.domain.repository.SimilarRepository
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.mapSimilarResponseToPage

class SimilarRepositoryImpl(private val remoteService: RemoteService) : SimilarRepository {
    override suspend fun getFirstPage(contentId: Int, mediaType: MediaType): SimilarPage =
            getPage(contentId, mediaType, 1)

    override fun getSimilarContent(contentId: Int, mediaType: MediaType, firstPage: SimilarPage) = Pager(
            config = PagingConfig(pageSize = 20, initialLoadSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { SimilarPagingSource(firstPage) { page -> getPage(contentId, mediaType, page) } }
    ).flow

    private suspend fun getPage(contentId: Int, mediaType: MediaType, page: Int): SimilarPage {
        val response = when (mediaType) {
            MediaType.MOVIE_TYPE -> remoteService.getSimilarMovies(contentId, page)
            MediaType.TV_TYPE -> remoteService.getSimilarShows(contentId, page)
            MediaType.PERSON_TYPE -> error("Similar content requires a movie or TV show")
        }
        return response.mapSimilarResponseToPage()
    }
}

package com.aldiprahasta.tmdb.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.aldiprahasta.tmdb.domain.model.GenreMovieQuery
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.utils.mapMovieResponseToMovieDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMoviesByGenre(private val movieRepository: MovieRepository) {
    operator fun invoke(query: GenreMovieQuery): Flow<PagingData<MovieDomainModel>> =
            movieRepository.getMoviesByGenre(query)
                    .map { pagingData ->
                        pagingData.map { it.mapMovieResponseToMovieDomainModel() }
                    }
}

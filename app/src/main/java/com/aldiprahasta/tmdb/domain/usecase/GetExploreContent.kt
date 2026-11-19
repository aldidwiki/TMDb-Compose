package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.ExploreQuery
import com.aldiprahasta.tmdb.domain.repository.ExploreRepository

class GetExploreContent(private val repository: ExploreRepository) {
    operator fun invoke(query: ExploreQuery) = repository.explore(query)
}

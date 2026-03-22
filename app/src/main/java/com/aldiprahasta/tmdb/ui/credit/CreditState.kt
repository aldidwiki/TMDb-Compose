package com.aldiprahasta.tmdb.ui.credit

import androidx.compose.runtime.Immutable
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel

@Immutable
data class CreditState(
        val selectedGenres: Set<GenreDomainModel> = emptySet(),
        val selectedSortingChip: String = "",

        val searchQuery: String = "",

        val movieGenreDomainModels: List<GenreDomainModel> = emptyList(),
        val tvGenreDomainModels: List<GenreDomainModel> = emptyList(),

        val castDomainModels: List<CastDomainModel> = emptyList(),
        val filteredCasts: List<CastDomainModel> = emptyList(),

        val isLoading: Boolean = false,
        val creditError: Throwable? = null,
        val creditErrorMsg: String? = null
)

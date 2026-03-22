package com.aldiprahasta.tmdb.ui.credit

import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel

sealed interface CreditEvent {
    data class Initialize(val contentId: Int, val contentType: String) : CreditEvent

    data class OnSortingChipClicked(
            val sortingComparator: Comparator<CastDomainModel>,
            val casts: List<CastDomainModel>,
            val contentType: String,
            val selectedSortingChip: String
    ) : CreditEvent

    data class OnGenreFilterChipClicked(
            val selectedGenres: Set<GenreDomainModel>,
            val casts: List<CastDomainModel>,
            val selectedGenre: GenreDomainModel
    ) : CreditEvent

    data class OnGenreFilterApplied(
            val selectedGenres: Set<GenreDomainModel>,
            val casts: List<CastDomainModel>
    ) : CreditEvent

    data class OnSearchFilterApplied(
            val query: String,
            val casts: List<CastDomainModel>
    ) : CreditEvent
}
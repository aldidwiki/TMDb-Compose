package com.aldiprahasta.tmdb.domain.model

data class SearchPage(
        val results: List<SearchDomainModel>,
        val totalPages: Int,
        val totalResults: Int
)

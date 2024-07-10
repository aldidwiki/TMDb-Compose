package com.aldiprahasta.tmdb.domain.model

data class SearchDomainModel(
        val id: Int,
        val name: String,
        val imagePath: String,
        val releaseDate: String,
        val mediaType: String,
        val knownFor: String,
        val popularity: Double
)

package com.aldiprahasta.tmdb.domain.model

data class FavoriteDomainModel(
        val favoriteId: Int,
        val title: String,
        val releaseDate: String,
        val imagePoster: String?,
        val mediaType: String,
        val addedAt: Long,
        val voteAverage: Double? = null,
        val voteCount: Int? = null,
        val genres: String? = null,
        val seasonCount: Int? = null,
        val knownFor: String? = null
)

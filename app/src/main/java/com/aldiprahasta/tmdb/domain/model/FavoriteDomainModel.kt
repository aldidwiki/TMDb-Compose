package com.aldiprahasta.tmdb.domain.model

data class FavoriteDomainModel(
        val favoriteId: Int,
        val title: String,
        val releaseDate: String,
        val imagePoster: String?,
        val mediaType: String
)

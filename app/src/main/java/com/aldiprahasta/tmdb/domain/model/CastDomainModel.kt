package com.aldiprahasta.tmdb.domain.model

data class CastDomainModel(
        val id: Int,
        val name: String,
        val characterName: String,
        val imagePath: String?,
        val order: Int,
        val mediaType: String?
)

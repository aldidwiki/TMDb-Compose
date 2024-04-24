package com.aldiprahasta.tmdb.domain.model

data class CastDomainModel(
        val name: String,
        val characterName: String,
        val profilePath: String?,
        val order: Int
)

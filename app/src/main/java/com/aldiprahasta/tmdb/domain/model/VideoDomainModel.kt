package com.aldiprahasta.tmdb.domain.model

data class VideoDomainModel(
        val id: String,
        val name: String,
        val site: String,
        val type: String,
        val key: String,
        val isOfficial: Boolean
)

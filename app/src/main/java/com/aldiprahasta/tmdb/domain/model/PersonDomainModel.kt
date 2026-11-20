package com.aldiprahasta.tmdb.domain.model

data class PersonDomainModel(
        val id: Int,
        val profilePath: String?,
        val name: String,
        val birthDay: String,
        val deathDay: String,
        val gender: String,
        val biography: String,
        val knownFor: String,
        val age: Int?,
        val placeOfBirth: String,
        val externalIds: ExternalIdDomainModel,
        val credits: List<CastDomainModel>,
        val alsoKnownAs: List<String> = emptyList()
)

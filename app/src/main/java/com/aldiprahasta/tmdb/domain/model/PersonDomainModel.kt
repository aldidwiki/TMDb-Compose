package com.aldiprahasta.tmdb.domain.model

data class PersonDomainModel(
        val profilePath: String?,
        val name: String,
        val birthDay: String,
        val deathDay: String,
        val gender: String,
        val biography: String,
        val knownFor: String,
        val age: String,
        val placeOfBirth: String,
        val externalIds: ExternalIdDomainModel,
        val credits: List<CastDomainModel>
)

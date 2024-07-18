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
        val age: String,
        val placeOfBirth: String,
        val externalIds: ExternalIdDomainModel,
        var credits: List<CastDomainModel>
)

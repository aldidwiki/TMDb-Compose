package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel

fun PersonResponse.mapPersonResponseToPersonDomainModel(): PersonDomainModel {
    return PersonDomainModel(
            id = id,
            profilePath = profilePath,
            name = name ?: "",
            birthDay = birthday?.convertDate() ?: "",
            deathDay = deathday?.convertDate() ?: "",
            gender = gender?.formatGender() ?: "-",
            biography = biography ?: "-",
            knownFor = knownForDepartment ?: "-",
            age = birthday?.let { getAge(it, deathday) } ?: "-",
            placeOfBirth = placeOfBirth ?: "-",
            externalIds = externalIds.mapExternalIdResponseToExternalIdDomainModel(),
            credits = combinedCredits?.mapCreditResponseToCastDomainModelList(MediaType.PERSON_TYPE)
                    ?: emptyList(),
            alsoKnownAs = alsoKnownAs.orEmpty().filter { it.isNotBlank() }.distinct()
    )
}

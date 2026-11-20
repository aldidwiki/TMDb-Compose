package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

fun PersonResponse.mapPersonResponseToPersonDomainModel(): PersonDomainModel {
    val birthDate = birthday.toPersonDate()
    val deathDate = deathday.toPersonDate()
    val ageReference = if (deathday.isNullOrBlank()) GregorianCalendar() else deathDate
    val formatter = SimpleDateFormat(Constant.APP_DATE_FORMAT, Locale.getDefault())

    return PersonDomainModel(
            id = id,
            profilePath = profilePath,
            name = name ?: "",
            birthDay = birthDate?.let { formatter.format(it.time) }.orEmpty(),
            deathDay = deathDate?.let { formatter.format(it.time) }.orEmpty(),
            gender = gender?.formatGender() ?: "-",
            biography = biography ?: "-",
            knownFor = knownForDepartment ?: "-",
            age = if (ageReference != null) birthDate?.ageOn(ageReference) else null,
            placeOfBirth = placeOfBirth ?: "-",
            externalIds = externalIds.mapExternalIdResponseToExternalIdDomainModel(),
            credits = combinedCredits?.mapCreditResponseToCastDomainModelList(MediaType.PERSON_TYPE)
                    ?: emptyList(),
            alsoKnownAs = alsoKnownAs.orEmpty().filter { it.isNotBlank() }.distinct()
    )
}

private fun String?.toPersonDate(): Calendar? {
    if (isNullOrBlank()) return null

    val parser = SimpleDateFormat(Constant.SOURCE_DATE_FORMAT, Locale.US).apply { isLenient = false }
    val position = ParsePosition(0)
    val date = parser.parse(this, position) ?: return null
    if (position.index != length) return null

    return GregorianCalendar().apply { time = date }
}

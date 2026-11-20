package com.aldiprahasta.tmdb.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun String.convertDate(
        inFormat: String = Constant.SOURCE_DATE_FORMAT,
        outFormat: String = Constant.APP_DATE_FORMAT
): String {
    val parser = SimpleDateFormat(inFormat, Locale.getDefault())
    val formatter = SimpleDateFormat(outFormat, Locale.getDefault())

    val date = parser.parse(this.ifEmpty { return this })
    return date?.let {
        formatter.format(it)
    } ?: this
}

internal fun Calendar.ageOn(referenceDate: Calendar): Int? {
    val birthMonth = get(Calendar.MONTH)
    val referenceMonth = referenceDate.get(Calendar.MONTH)
    val hasReachedBirthday = referenceMonth > birthMonth ||
            (referenceMonth == birthMonth && referenceDate.get(Calendar.DAY_OF_MONTH) >= get(Calendar.DAY_OF_MONTH))
    val yearDifference = referenceDate.get(Calendar.YEAR) - get(Calendar.YEAR)
    return (if (hasReachedBirthday) yearDifference else yearDifference - 1).takeIf { it >= 0 }
}

fun getCharacterAge(birthday: String, releaseDate: String?): String {
    val contentReleaseDate = SimpleDateFormat(Constant.APP_DATE_FORMAT, Locale.getDefault()).also {
        it.calendar.time = it.parse(releaseDate ?: "") ?: java.util.Date()
    }.calendar

    val birthDate = SimpleDateFormat(Constant.APP_DATE_FORMAT, Locale.getDefault()).also {
        it.calendar.time = it.parse(birthday) ?: java.util.Date()
    }.calendar

    var characterAge = contentReleaseDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

    if (contentReleaseDate.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
        characterAge -= 1
    }

    return characterAge.toString()
}

internal fun String?.releaseYear(): String? = this?.take(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) }

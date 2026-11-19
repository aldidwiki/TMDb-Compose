package com.aldiprahasta.tmdb.utils

import java.sql.Date
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

fun getAge(birthday: String, deathDay: String?): String {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).calendar
    date.time = Date.valueOf(birthday)

    val currentDate = Calendar.getInstance()
    deathDay?.let { currentDate.time = Date.valueOf(it) }
    var age = currentDate.get(Calendar.YEAR) - date.get(Calendar.YEAR)

    if (currentDate.get(Calendar.DAY_OF_YEAR) < date.get(Calendar.DAY_OF_YEAR))
        age -= 1

    return age.toString()
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

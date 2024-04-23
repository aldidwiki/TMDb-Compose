package com.aldiprahasta.tmdb.utils

import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

fun String.convertDate(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    val date = parser.parse(this)
    return date?.let {
        formatter.format(it)
    } ?: this
}

fun Int?.runtimeFormat(): String {
    var runtimeText = ""
    this?.let {
        val hours = this / 60
        val minutes = this % 60

        runtimeText = if (hours <= 0) minutes.toString() + "m"
        else hours.toString() + "h " + minutes.toString() + "m"
    }

    return runtimeText
}

fun Double?.formatVoteAverage(): Int {
    val format = String.format("%.1f", this).toDouble() * 10
    return format.roundToInt()
}

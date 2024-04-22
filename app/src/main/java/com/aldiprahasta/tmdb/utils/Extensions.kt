package com.aldiprahasta.tmdb.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun String.convertDate(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    val date = parser.parse(this)
    return date?.let {
        formatter.format(it)
    } ?: this
}
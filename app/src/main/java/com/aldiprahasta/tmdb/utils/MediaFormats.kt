package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponseModel
import java.text.NumberFormat
import java.util.Locale

fun formatMovieRating(rating: Double): String = java.text.NumberFormat.getNumberInstance().apply {
    minimumFractionDigits = 1
    maximumFractionDigits = 1
}.format(rating)

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

fun List<GenreResponseModel>?.convertGenreToSingleText(): String {
    val outputGenre = StringBuilder()
    this?.sortedBy { it.name }?.forEachIndexed { index, genresItem ->
        outputGenre.append(genresItem.name)
        if (index < this.lastIndex) {
            outputGenre.append(", ")
        }
    }

    return outputGenre.toString()
}

fun Long.formatCurrency(): String {
    val currency = NumberFormat.getCurrencyInstance(Locale.US)
    return currency.format(this)
}

fun String.getLanguageDisplayName(): String = if (this.isNotEmpty()) {
    val locale = Locale.Builder()
            .setLanguage(this)
            .build()
    locale.displayLanguage
} else {
    this
}

fun Int?.formatGender(): String = when (this) {
    1 -> "Female"
    2 -> "Male"
    else -> "Not Sure"
}

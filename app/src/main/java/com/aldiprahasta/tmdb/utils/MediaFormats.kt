package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponseModel
import java.text.NumberFormat

fun formatMovieRating(rating: Double): String = NumberFormat.getNumberInstance().apply {
    minimumFractionDigits = 1
    maximumFractionDigits = 1
}.format(rating)

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

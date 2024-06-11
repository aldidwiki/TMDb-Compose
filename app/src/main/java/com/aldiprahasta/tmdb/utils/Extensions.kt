package com.aldiprahasta.tmdb.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import java.sql.Date
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

fun String.convertDate(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    val date = parser.parse(this.ifEmpty { return this })
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

fun List<GenreResponse>?.convertGenreToSingleText(): String {
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
    Locale(this).displayLanguage
} else {
    this
}

fun Context.shareIt(text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
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

fun Int?.formatGender(): String = when (this) {
    1 -> "Female"
    2 -> "Male"
    else -> "Not Sure"
}

suspend fun Context.getImageBitmap(imagePath: String): Bitmap {
    val loader = ImageLoader(this)
    val request = ImageRequest.Builder(this)
            .data("https://image.tmdb.org/t/p/w500/$imagePath")
            .allowHardware(false)
            .build()

    val result = (loader.execute(request) as SuccessResult).drawable
    return (result as BitmapDrawable).bitmap
}

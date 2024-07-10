package com.aldiprahasta.tmdb.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.PagingErrorFooter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import java.sql.Date
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

fun String.convertDate(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

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
    val format = String.format(Locale.getDefault(), "%.1f", this).toDouble() * 10
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

fun <T> Flow<UiState<T>>.delayAfterLoading(timeMillis: Long): Flow<UiState<T>> = onEach { state ->
    if (state != UiState.Loading) delay(timeMillis)
}

fun <T : Any> LazyListScope.setupPagingLoadState(lazyPagingItems: LazyPagingItems<T>) {
    lazyPagingItems.apply {
        when {
            loadState.refresh is LoadState.Loading -> {
                item { LoadingScreen(modifier = Modifier.fillParentMaxSize()) }
            }

            loadState.refresh is LoadState.Error -> {
                val error = loadState.refresh as LoadState.Error
                item {
                    ErrorScreen(
                            errorMessage = error.error.localizedMessage ?: "No Data Found",
                            modifier = Modifier.fillParentMaxSize()
                    )
                }
            }

            loadState.append is LoadState.Loading -> {
                item {
                    LoadingScreen(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                            indicatorSizeInDp = 30.dp
                    )
                }
            }

            loadState.append is LoadState.Error -> {
                val error = loadState.append as LoadState.Error
                item {
                    PagingErrorFooter(
                            errorMessage = error.error.localizedMessage,
                            onRetryClicked = { retry() }
                    )
                }
            }
        }
    }
}

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

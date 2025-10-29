package com.aldiprahasta.tmdb.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponseModel
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.PagingErrorFooter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import java.sql.Date
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

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
    Locale(this).displayLanguage
} else {
    this
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

fun Int?.formatGender(): String = when (this) {
    1 -> "Female"
    2 -> "Male"
    else -> "Not Sure"
}

suspend fun Context.getImageBitmap(imagePath: String): Bitmap? {
    val loader = ImageLoader(this)
    val request = ImageRequest.Builder(this)
            .data("https://image.tmdb.org/t/p/w185/$imagePath")
            .allowHardware(false)
            .build()

    val result = (loader.execute(request) as? SuccessResult)?.drawable
    return (result as? BitmapDrawable)?.bitmap
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
                item {
                    ErrorScreen(
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

fun Context.openBrowser(url: String) {
    CustomTabsIntent.Builder().apply {
        setShareState(CustomTabsIntent.SHARE_STATE_OFF)
    }.build().launchUrl(this, Uri.parse(url))
}

fun <T, R, E> Flow<Triple<UiState<T>, UiState<R>, UiState<E>>>.asUiStateTriple(): Flow<UiState<Triple<T, R, E>>> = transform { state ->
    val (stateFirst, stateSecond, stateThird) = state
    var stateFirstData: T? = null
    var stateSecondData: R? = null
    var stateThirdData: E? = null

    when (stateFirst) {
        is UiState.Loading -> emit(UiState.Loading)
        is UiState.Error -> {
            emit(UiState.Error(stateFirst.throwable, stateFirst.errorMessage))
            return@transform
        }

        is UiState.Success -> {
            stateFirstData = stateFirst.data
        }
    }

    when (stateSecond) {
        is UiState.Loading -> emit(UiState.Loading)
        is UiState.Error -> {
            emit(UiState.Error(stateSecond.throwable, stateSecond.errorMessage))
            return@transform
        }

        is UiState.Success -> {
            stateSecondData = stateSecond.data
        }
    }

    when (stateThird) {
        is UiState.Loading -> emit(UiState.Loading)
        is UiState.Error -> {
            emit(UiState.Error(stateThird.throwable, stateThird.errorMessage))
            return@transform
        }

        is UiState.Success -> {
            stateThirdData = stateThird.data
        }
    }

    if (stateFirstData != null && stateSecondData != null && stateThirdData != null) {
        emit(UiState.Success(Triple(stateFirstData, stateSecondData, stateThirdData)))
    }
}

fun <T> Flow<UiState<T>>.toStateFlow(
        scope: CoroutineScope,
        stopTimeoutMillis: Long = 5000L
): StateFlow<UiState<T>> = this.stateIn(
        scope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis),
        UiState.Loading
)

fun isColorLight(colorInt: Int): Boolean {
    // Returns a value between 0.0 (darkest) and 1.0 (lightest)
    // A common threshold is 0.5
    return ColorUtils.calculateLuminance(colorInt) > 0.5
}

/**
 * A composable function to calculate a fractional height based on the current
 * window's available space, following adaptive layout best practices.
 * @param fraction The percentage of the window height to use (e.g., 0.75f for 75%).
 * @return The calculated height in Dp.
 */
@Composable
fun windowHeightFraction(fraction: Float): Dp {
    // This is the core logic from your request:
    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize

    // Convert the pixel height to Dp and apply the fraction
    return with(density) {
        // We use Int.toDp() on the pixel height, then multiply by the fraction.
        containerSize.height.toDp() * fraction
    }
}

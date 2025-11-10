package com.aldiprahasta.tmdb.utils

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.core.os.BundleCompat
import androidx.core.view.WindowCompat
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
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
    val locale = Locale.Builder()
            .setLanguage(this)
            .build()
    locale.displayLanguage
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

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? {
    return BundleCompat.getParcelableArrayList<T>(this, key, T::class.java)
}

fun Context.openBrowser(url: String) {
    CustomTabsIntent.Builder().apply {
        setShareState(CustomTabsIntent.SHARE_STATE_OFF)
    }.build().launchUrl(this, url.toUri())
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

/**
 * Function to extract a name based on its word count.
 * - If the name consists of > 2 words, it takes only the first word.
 * - If the name consists of <= 2 words, it returns the entire name.
 * @param fullName The person's full name (String).
 * @return The abbreviated name or the full name (String).
 */
fun extractNameByWordCount(fullName: String): String {
    // 1. Clean up leading/trailing spaces and split based on one or more spaces
    val nameWords = fullName.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() } // Removes empty elements from excessive spaces

    val wordCount = nameWords.size

    return when {
        // Case: More than 2 words (e.g., Three, Four, etc.)
        wordCount > 2 -> {
            // Take only the first word
            nameWords.first()
        }

        // Case: 1 or 2 words
        else -> {
            // Join all words back into a single string
            nameWords.joinToString(" ")
        }
    }
}

fun isColorLight(colorInt: Int): Boolean {
    // Returns a value between 0.0 (darkest) and 1.0 (lightest)
    // A common threshold is 0.5
    return ColorUtils.calculateLuminance(colorInt) > 0.5
}

@Composable
fun DynamicSystemBarColor(topBarColor: Color) {
    val view = LocalView.current
    // Check if we are in an editable preview (optional, good practice)
    if (view.isInEditMode) return

    // Calculate the integer representation of the color for the luminance check
    val colorInt = topBarColor.toArgb()
    val isLight = isColorLight(colorInt)

    DisposableEffect(isLight) {
        val window = (view.context as android.app.Activity).window
        val insetsController = WindowCompat.getInsetsController(window, view)

        insetsController.isAppearanceLightStatusBars = isLight

        onDispose {
            insetsController.isAppearanceLightStatusBars = false
        }
    }
}

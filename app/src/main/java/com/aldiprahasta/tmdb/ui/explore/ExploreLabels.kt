package com.aldiprahasta.tmdb.ui.explore

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ExploreDepartment
import com.aldiprahasta.tmdb.domain.model.ExploreKnownFor
import com.aldiprahasta.tmdb.domain.model.ExplorePreset
import com.aldiprahasta.tmdb.domain.model.ExploreSort
import com.aldiprahasta.tmdb.domain.model.ExploreTab
import com.aldiprahasta.tmdb.domain.model.ExploreYears

@get:StringRes
internal val ExploreTab.label: Int get() = when (this) {
    ExploreTab.ALL -> R.string.explore_tab_all
    ExploreTab.MOVIES -> R.string.explore_tab_movies
    ExploreTab.TV -> R.string.explore_tab_shows
    ExploreTab.PEOPLE -> R.string.explore_tab_people
}

@get:StringRes
internal val ExploreTab.subtitle: Int get() = when (this) {
    ExploreTab.ALL -> R.string.explore_all_subtitle
    ExploreTab.MOVIES -> R.string.explore_movies_subtitle
    ExploreTab.TV -> R.string.explore_shows_subtitle
    ExploreTab.PEOPLE -> R.string.explore_people_subtitle
}

@get:StringRes
internal val ExploreTab.loadingTitle: Int get() = when (this) {
    ExploreTab.ALL -> R.string.explore_loading_all
    ExploreTab.MOVIES -> R.string.explore_loading_movies
    ExploreTab.TV -> R.string.explore_loading_shows
    ExploreTab.PEOPLE -> R.string.explore_loading_people
}

@get:StringRes
internal val ExploreTab.errorTitle: Int get() = when (this) {
    ExploreTab.ALL -> R.string.explore_error_all
    ExploreTab.MOVIES -> R.string.explore_error_movies
    ExploreTab.TV -> R.string.explore_error_shows
    ExploreTab.PEOPLE -> R.string.explore_error_people
}

@get:StringRes
internal val ExploreTab.filterTitle: Int get() = when (this) {
    ExploreTab.ALL -> R.string.explore_filter_title
    ExploreTab.MOVIES -> R.string.explore_filter_movies
    ExploreTab.TV -> R.string.explore_filter_tv
    ExploreTab.PEOPLE -> R.string.explore_filter_people
}

internal fun ExploreTab.presets(): List<ExplorePreset> = when (this) {
    ExploreTab.ALL -> emptyList()
    ExploreTab.MOVIES -> listOf(ExplorePreset.NOW_PLAYING, ExplorePreset.MOVIE_TOP_RATED, ExplorePreset.UPCOMING)
    ExploreTab.TV -> listOf(ExplorePreset.AIRING_TODAY, ExplorePreset.ON_THE_AIR, ExplorePreset.TV_TOP_RATED)
    ExploreTab.PEOPLE -> listOf(ExplorePreset.PEOPLE_POPULAR, ExplorePreset.PEOPLE_TRENDING, ExplorePreset.PEOPLE_NAME)
}

internal fun ExploreTab.sorts(): List<ExploreSort> = if (this == ExploreTab.PEOPLE) {
    listOf(ExploreSort.POPULAR, ExploreSort.TRENDING, ExploreSort.NAME, ExploreSort.KNOWN_FOR_MOVIES, ExploreSort.KNOWN_FOR_TV)
} else {
    listOf(ExploreSort.POPULAR, ExploreSort.NEWEST, ExploreSort.HIGHEST_RATED, ExploreSort.TITLE, ExploreSort.MOST_VOTED)
}

@get:StringRes
internal val ExplorePreset.label: Int get() = when (this) {
    ExplorePreset.NOW_PLAYING -> R.string.explore_now_playing
    ExplorePreset.MOVIE_TOP_RATED, ExplorePreset.TV_TOP_RATED -> R.string.explore_top_rated
    ExplorePreset.UPCOMING -> R.string.explore_upcoming
    ExplorePreset.AIRING_TODAY -> R.string.explore_airing_today
    ExplorePreset.ON_THE_AIR -> R.string.explore_on_the_air
    ExplorePreset.PEOPLE_POPULAR -> R.string.explore_popular
    ExplorePreset.PEOPLE_TRENDING -> R.string.explore_trending
    ExplorePreset.PEOPLE_NAME -> R.string.explore_az
}

@get:StringRes
internal val ExploreSort.label: Int get() = when (this) {
    ExploreSort.POPULAR -> R.string.explore_popular
    ExploreSort.NEWEST -> R.string.explore_newest
    ExploreSort.HIGHEST_RATED -> R.string.explore_highest_rated
    ExploreSort.TITLE -> R.string.explore_title_az
    ExploreSort.MOST_VOTED -> R.string.explore_most_voted
    ExploreSort.TRENDING -> R.string.explore_trending
    ExploreSort.NAME -> R.string.explore_name_az
    ExploreSort.KNOWN_FOR_MOVIES -> R.string.explore_known_movies
    ExploreSort.KNOWN_FOR_TV -> R.string.explore_known_tv
}

@get:StringRes
internal val ExploreSort.shortLabel: Int get() = when (this) {
    ExploreSort.HIGHEST_RATED -> R.string.explore_rated_short
    ExploreSort.MOST_VOTED -> R.string.explore_voted_short
    ExploreSort.KNOWN_FOR_MOVIES -> R.string.explore_tab_movies
    ExploreSort.KNOWN_FOR_TV -> R.string.explore_tv_short
    else -> label
}

@get:StringRes
internal val ExploreDepartment.label: Int get() = when (this) {
    ExploreDepartment.ACTING -> R.string.explore_acting
    ExploreDepartment.DIRECTING -> R.string.explore_directing
    ExploreDepartment.WRITING -> R.string.explore_writing
    ExploreDepartment.PRODUCTION -> R.string.explore_production
    ExploreDepartment.SOUND -> R.string.explore_sound
}

@get:StringRes
internal val ExploreKnownFor.label: Int get() = when (this) {
    ExploreKnownFor.MOVIES -> R.string.explore_tab_movies
    ExploreKnownFor.TV -> R.string.explore_tab_shows
    ExploreKnownFor.BOTH -> R.string.explore_known_both
}

internal fun Int.asYearText(): String = toString().padStart(4, '0')

@Composable
internal fun ExploreYears?.label(): String = when {
    this == null -> stringResource(R.string.explore_year_any)
    from == to -> from.asYearText()
    else -> from.asYearText() + "–" + to.asYearText()
}

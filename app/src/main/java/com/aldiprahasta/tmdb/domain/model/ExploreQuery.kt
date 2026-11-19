package com.aldiprahasta.tmdb.domain.model

import com.aldiprahasta.tmdb.utils.MediaType

enum class ExploreTab { ALL, MOVIES, TV, PEOPLE }

enum class ExploreSort {
    POPULAR, NEWEST, HIGHEST_RATED, TITLE, MOST_VOTED, TRENDING, NAME, KNOWN_FOR_MOVIES, KNOWN_FOR_TV
}

enum class ExplorePreset(val defaultSort: ExploreSort) {
    NOW_PLAYING(ExploreSort.POPULAR),
    MOVIE_TOP_RATED(ExploreSort.HIGHEST_RATED),
    UPCOMING(ExploreSort.POPULAR),
    AIRING_TODAY(ExploreSort.POPULAR),
    ON_THE_AIR(ExploreSort.POPULAR),
    TV_TOP_RATED(ExploreSort.HIGHEST_RATED),
    PEOPLE_POPULAR(ExploreSort.POPULAR),
    PEOPLE_TRENDING(ExploreSort.TRENDING),
    PEOPLE_NAME(ExploreSort.NAME)
}

enum class ExploreDepartment(val apiName: String) {
    ACTING("Acting"), DIRECTING("Directing"), WRITING("Writing"), PRODUCTION("Production"), SOUND("Sound")
}

enum class ExploreKnownFor { MOVIES, TV, BOTH }

data class ExploreYears(val from: Int, val to: Int = from) {
    init {
        require(from in 1..9999 && to in from..9999)
    }
}

data class ExploreGenre(val id: Int, val name: String, val mediaTypes: Set<MediaType>)

data class ExploreFilters(
        val genres: Set<ExploreGenre> = emptySet(),
        val years: ExploreYears? = null,
        val minimumRating: Int? = null,
        val departments: Set<ExploreDepartment> = emptySet(),
        val knownFor: ExploreKnownFor? = null
) {
    val hasTitleFilters: Boolean get() = genres.isNotEmpty() || years != null || minimumRating != null
    val hasPeopleFilters: Boolean get() = departments.isNotEmpty() || knownFor != null
}

data class ExploreQuery(
        val tab: ExploreTab = ExploreTab.ALL,
        val preset: ExplorePreset? = null,
        val sort: ExploreSort? = null,
        val filters: ExploreFilters = ExploreFilters()
) {
    val order: ExploreSort get() = sort ?: preset?.defaultSort ?: ExploreSort.POPULAR
    val includesPeople: Boolean get() = tab == ExploreTab.PEOPLE ||
            (tab == ExploreTab.ALL && !filters.hasTitleFilters && order in listOf(ExploreSort.POPULAR, ExploreSort.TITLE))
}

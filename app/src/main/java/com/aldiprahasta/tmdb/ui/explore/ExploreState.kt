package com.aldiprahasta.tmdb.ui.explore

import com.aldiprahasta.tmdb.domain.model.ExploreGenre
import com.aldiprahasta.tmdb.domain.model.ExploreQuery
import com.aldiprahasta.tmdb.domain.model.ExploreTab
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState

internal data class ExploreState(
        val tab: ExploreTab = ExploreTab.ALL,
        val queries: Map<ExploreTab, ExploreQuery> = emptyMap(),
        val movieGenres: UiState<List<GenreDomainModel>> = UiState.Loading,
        val tvGenres: UiState<List<GenreDomainModel>> = UiState.Loading
) {
    val query: ExploreQuery get() = queries[tab] ?: ExploreQuery(tab)
    val movieGenreNames: Map<Int, String> get() = movieGenres.items().associate { it.id to it.name }
    val tvGenreNames: Map<Int, String> get() = tvGenres.items().associate { it.id to it.name }

    fun genreOptions(): List<ExploreGenre> {
        val movies = movieGenreNames.takeIf { tab != ExploreTab.TV }.orEmpty()
        val tv = tvGenreNames.takeIf { tab != ExploreTab.MOVIES }.orEmpty()
        return (movies.keys + tv.keys).map { id ->
            ExploreGenre(
                    id = id,
                    name = movies[id] ?: tv.getValue(id),
                    mediaTypes = buildSet {
                        if (id in movies) add(MediaType.MOVIE_TYPE)
                        if (id in tv) add(MediaType.TV_TYPE)
                    }
            )
        }.sortedBy { it.name }
    }

    fun genreStates(): List<UiState<List<GenreDomainModel>>> = when (tab) {
        ExploreTab.MOVIES -> listOf(movieGenres)
        ExploreTab.TV -> listOf(tvGenres)
        ExploreTab.ALL -> listOf(movieGenres, tvGenres)
        ExploreTab.PEOPLE -> emptyList()
    }
}

private fun UiState<List<GenreDomainModel>>.items(): List<GenreDomainModel> =
        (this as? UiState.Success)?.data.orEmpty()

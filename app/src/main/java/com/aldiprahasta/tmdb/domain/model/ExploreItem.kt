package com.aldiprahasta.tmdb.domain.model

import com.aldiprahasta.tmdb.utils.MediaType

data class ExploreItem(
        val id: Int,
        val mediaType: MediaType,
        val title: String,
        val imagePath: String?,
        val releaseDate: String?,
        val rating: Double?,
        val voteCount: Int,
        val popularity: Double,
        val genreIds: List<Int>,
        val overview: String,
        val department: String?,
        val knownFor: List<MediaType>
) {
    val key: String get() = mediaType.name + ":" + id
}

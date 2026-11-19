package com.aldiprahasta.tmdb.data.source.local.entity

import androidx.room.Entity

@Entity(tableName = "favorite_table", primaryKeys = ["id", "mediaType"])
data class FavoriteEntity(
        val id: Int,
        val name: String,
        val releaseDate: String,
        val imagePath: String?,
        val mediaType: String,
        val addedAt: Long,
        val voteAverage: Double? = null,
        val voteCount: Int? = null,
        val genres: String? = null,
        val seasonCount: Int? = null,
        val knownFor: String? = null
)

package com.aldiprahasta.tmdb.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class FavoriteEntity(
        @PrimaryKey val id: Int,
        val title: String,
        val releaseDate: String,
        val imagePath: String?,
        val mediaType: String
)

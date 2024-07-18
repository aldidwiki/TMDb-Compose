package com.aldiprahasta.tmdb.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aldiprahasta.tmdb.data.source.local.entity.FavoriteEntity

@Database(
        entities = [
            FavoriteEntity::class
        ],
        version = 2
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val DB_NAME = "tmdb_app"
    }
}
package com.aldiprahasta.tmdb.data.source.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aldiprahasta.tmdb.data.source.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_table")
    suspend fun getAllFavorites(): List<FavoriteEntity>

    @Query("DELETE FROM favorite_table WHERE id = :favoriteId")
    suspend fun deleteFavorite(favoriteId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteEntity: FavoriteEntity)
}
package com.aldiprahasta.tmdb.data.source.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aldiprahasta.tmdb.data.source.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_table")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT id FROM favorite_table WHERE id = :id")
    fun isContentFavorite(id: Int): Flow<Boolean>

    @Query("DELETE FROM favorite_table WHERE id = :favoriteId")
    suspend fun deleteFavorite(favoriteId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteEntity: FavoriteEntity)
}
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

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_table WHERE id = :id AND mediaType = :mediaType)")
    fun isContentFavorite(id: Int, mediaType: String): Flow<Boolean>

    @Query("DELETE FROM favorite_table WHERE id = :favoriteId AND mediaType = :mediaType")
    suspend fun deleteFavorite(favoriteId: Int, mediaType: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favoriteEntity: FavoriteEntity)
}

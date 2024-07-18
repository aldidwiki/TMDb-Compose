package com.aldiprahasta.tmdb.di

import android.content.Context
import androidx.room.Room
import com.aldiprahasta.tmdb.data.repository.FavoriteRepositoryImpl
import com.aldiprahasta.tmdb.data.source.local.FavoriteDataSource
import com.aldiprahasta.tmdb.data.source.local.database.FavoriteDao
import com.aldiprahasta.tmdb.data.source.local.database.LocalDatabase
import com.aldiprahasta.tmdb.domain.repository.FavoriteRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val localeModule = module {
    singleOf(::provideLocalDatabase)
    singleOf(::provideFavoriteDao) { bind<FavoriteDao>() }

    singleOf(::FavoriteDataSource)
    singleOf(::FavoriteRepositoryImpl) { bind<FavoriteRepository>() }
}

private fun provideLocalDatabase(context: Context): LocalDatabase {
    return Room.databaseBuilder(
            context = context,
            klass = LocalDatabase::class.java,
            name = LocalDatabase.DB_NAME
    ).build()
}

private fun provideFavoriteDao(localDatabase: LocalDatabase): FavoriteDao {
    return localDatabase.favoriteDao()
}
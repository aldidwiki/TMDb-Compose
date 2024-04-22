package com.aldiprahasta.tmdb.di

import com.aldiprahasta.tmdb.data.repository.MovieRepositoryImpl
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun provideMovieRepository(movieRepositoryImpl: MovieRepositoryImpl): MovieRepository
}
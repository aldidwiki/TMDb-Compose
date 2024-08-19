package com.aldiprahasta.tmdb.di

import com.aldiprahasta.tmdb.data.repository.MovieRepositoryImpl
import com.aldiprahasta.tmdb.data.repository.PersonRepositoryImpl
import com.aldiprahasta.tmdb.data.repository.SearchRepositoryImpl
import com.aldiprahasta.tmdb.data.repository.TvRepositoryImpl
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.domain.repository.PersonRepository
import com.aldiprahasta.tmdb.domain.repository.SearchRepository
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::MovieRepositoryImpl) bind MovieRepository::class
    singleOf(::PersonRepositoryImpl) bind PersonRepository::class
    singleOf(::TvRepositoryImpl) bind TvRepository::class
    singleOf(::SearchRepositoryImpl) bind SearchRepository::class
}
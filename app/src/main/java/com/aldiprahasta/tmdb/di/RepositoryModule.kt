package com.aldiprahasta.tmdb.di

import com.aldiprahasta.tmdb.data.repository.MovieRepositoryImpl
import com.aldiprahasta.tmdb.data.repository.PersonRepositoryImpl
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.domain.repository.PersonRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::MovieRepositoryImpl) { bind<MovieRepository>() }
    singleOf(::PersonRepositoryImpl) { bind<PersonRepository>() }
}
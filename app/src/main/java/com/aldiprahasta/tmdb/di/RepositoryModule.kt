package com.aldiprahasta.tmdb.di

import com.aldiprahasta.tmdb.data.repository.ExploreRepositoryImpl
import com.aldiprahasta.tmdb.data.repository.MovieRepositoryImpl
import com.aldiprahasta.tmdb.data.repository.PersonRepositoryImpl
import com.aldiprahasta.tmdb.data.repository.SearchRepositoryImpl
import com.aldiprahasta.tmdb.data.repository.SimilarRepositoryImpl
import com.aldiprahasta.tmdb.data.repository.TvRepositoryImpl
import com.aldiprahasta.tmdb.domain.repository.ExploreRepository
import com.aldiprahasta.tmdb.domain.repository.MovieRepository
import com.aldiprahasta.tmdb.domain.repository.PersonRepository
import com.aldiprahasta.tmdb.domain.repository.SearchRepository
import com.aldiprahasta.tmdb.domain.repository.SimilarRepository
import com.aldiprahasta.tmdb.domain.repository.TvRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::ExploreRepositoryImpl) bind ExploreRepository::class
    singleOf(::MovieRepositoryImpl) bind MovieRepository::class
    singleOf(::PersonRepositoryImpl) bind PersonRepository::class
    singleOf(::TvRepositoryImpl) bind TvRepository::class
    singleOf(::SearchRepositoryImpl) bind SearchRepository::class
    singleOf(::SimilarRepositoryImpl) bind SimilarRepository::class
}

package com.aldiprahasta.tmdb.di

import com.aldiprahasta.tmdb.domain.usecase.GetMovieDetail
import com.aldiprahasta.tmdb.domain.usecase.GetPersonDetail
import com.aldiprahasta.tmdb.domain.usecase.GetPopularMovieList
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::GetPopularMovieList)
    factoryOf(::GetMovieDetail)
    factoryOf(::GetPersonDetail)
}
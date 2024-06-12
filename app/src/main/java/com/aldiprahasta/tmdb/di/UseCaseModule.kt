package com.aldiprahasta.tmdb.di

import com.aldiprahasta.tmdb.domain.usecase.GetMovieCredits
import com.aldiprahasta.tmdb.domain.usecase.GetMovieDetail
import com.aldiprahasta.tmdb.domain.usecase.GetPersonCredits
import com.aldiprahasta.tmdb.domain.usecase.GetPersonDetail
import com.aldiprahasta.tmdb.domain.usecase.GetPopularMovieList
import com.aldiprahasta.tmdb.domain.usecase.GetPopularTv
import com.aldiprahasta.tmdb.domain.usecase.GetTvCredits
import com.aldiprahasta.tmdb.domain.usecase.GetTvDetail
import com.aldiprahasta.tmdb.domain.usecase.wrapper.CreditWrapper
import com.aldiprahasta.tmdb.domain.usecase.wrapper.DetailWrapper
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::GetPopularMovieList)
    factoryOf(::GetMovieDetail)
    factoryOf(::GetPersonDetail)
    factoryOf(::GetMovieCredits)
    factoryOf(::GetPersonCredits)
    factoryOf(::GetPopularTv)
    factoryOf(::GetTvDetail)
    factoryOf(::GetTvCredits)
}

val useCaseWrapperModule = module {
    factoryOf(::CreditWrapper)
    factoryOf(::DetailWrapper)
}
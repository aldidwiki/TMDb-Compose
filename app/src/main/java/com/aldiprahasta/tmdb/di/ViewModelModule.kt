package com.aldiprahasta.tmdb.di

import com.aldiprahasta.tmdb.ui.credit.CreditViewModel
import com.aldiprahasta.tmdb.ui.details.ContentDetailViewModel
import com.aldiprahasta.tmdb.ui.movie.MovieViewModel
import com.aldiprahasta.tmdb.ui.person.PersonViewModel
import com.aldiprahasta.tmdb.ui.tv.TvViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MovieViewModel)
    viewModelOf(::ContentDetailViewModel)
    viewModelOf(::PersonViewModel)
    viewModelOf(::CreditViewModel)
    viewModelOf(::TvViewModel)
}
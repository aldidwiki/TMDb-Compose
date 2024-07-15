package com.aldiprahasta.tmdb.domain.usecase.wrapper

import com.aldiprahasta.tmdb.domain.usecase.GetMovieCredits
import com.aldiprahasta.tmdb.domain.usecase.GetMovieGenreList
import com.aldiprahasta.tmdb.domain.usecase.GetPersonCredits
import com.aldiprahasta.tmdb.domain.usecase.GetTvCredits
import com.aldiprahasta.tmdb.domain.usecase.GetTvGenreList

data class CreditWrapper(
        val getMovieCredits: GetMovieCredits,
        val getPersonCredits: GetPersonCredits,
        val getTvCredits: GetTvCredits,
        val getMovieGenreList: GetMovieGenreList,
        val getTvGenreList: GetTvGenreList
)

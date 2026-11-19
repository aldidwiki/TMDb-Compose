package com.aldiprahasta.tmdb.domain.usecase.wrapper

import com.aldiprahasta.tmdb.domain.usecase.GetMovieCredits
import com.aldiprahasta.tmdb.domain.usecase.GetTvCredits

data class CreditWrapper(
        val getMovieCredits: GetMovieCredits,
        val getTvCredits: GetTvCredits
)

package com.aldiprahasta.tmdb.domain.usecase.wrapper

import com.aldiprahasta.tmdb.domain.usecase.GetMovieDetail
import com.aldiprahasta.tmdb.domain.usecase.GetTvDetail

data class DetailWrapper(
        val getMovieDetail: GetMovieDetail,
        val getTvDetail: GetTvDetail
)

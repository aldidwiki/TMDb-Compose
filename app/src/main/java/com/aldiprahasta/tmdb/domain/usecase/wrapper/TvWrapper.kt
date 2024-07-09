package com.aldiprahasta.tmdb.domain.usecase.wrapper

import com.aldiprahasta.tmdb.domain.usecase.GetPopularTv
import com.aldiprahasta.tmdb.domain.usecase.GetTvSeasonDetail

data class TvWrapper(
        val getPopularTv: GetPopularTv,
        val getTvSeasonDetail: GetTvSeasonDetail
)

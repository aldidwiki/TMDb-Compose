package com.aldiprahasta.tmdb.domain.usecase.wrapper

import com.aldiprahasta.tmdb.domain.usecase.GetMovieDetail
import com.aldiprahasta.tmdb.domain.usecase.GetTvDetail
import com.aldiprahasta.tmdb.domain.usecase.favorite.DeleteFavorite
import com.aldiprahasta.tmdb.domain.usecase.favorite.InsertFavorite

data class DetailWrapper(
        val getMovieDetail: GetMovieDetail,
        val getTvDetail: GetTvDetail,
        val insertFavorite: InsertFavorite,
        val deleteFavorite: DeleteFavorite
)

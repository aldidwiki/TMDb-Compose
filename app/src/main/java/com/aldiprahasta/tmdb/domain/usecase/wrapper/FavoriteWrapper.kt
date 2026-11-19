package com.aldiprahasta.tmdb.domain.usecase.wrapper

import com.aldiprahasta.tmdb.domain.usecase.favorite.DeleteFavorite
import com.aldiprahasta.tmdb.domain.usecase.favorite.GetAllFavoriteList
import com.aldiprahasta.tmdb.domain.usecase.favorite.InsertFavorite

data class FavoriteWrapper(
        val getAllFavoriteList: GetAllFavoriteList,
        val deleteFavorite: DeleteFavorite,
        val insertFavorite: InsertFavorite
)

package com.aldiprahasta.tmdb.domain.usecase.wrapper

import com.aldiprahasta.tmdb.domain.usecase.GetPersonDetail
import com.aldiprahasta.tmdb.domain.usecase.favorite.DeleteFavorite
import com.aldiprahasta.tmdb.domain.usecase.favorite.GetFavoriteStatus
import com.aldiprahasta.tmdb.domain.usecase.favorite.InsertFavorite

data class PersonDetailWrapper(
        val getPersonDetail: GetPersonDetail,
        val insertFavorite: InsertFavorite,
        val deleteFavorite: DeleteFavorite,
        val getFavoriteStatus: GetFavoriteStatus
)

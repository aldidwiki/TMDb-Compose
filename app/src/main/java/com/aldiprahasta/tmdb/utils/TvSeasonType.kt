package com.aldiprahasta.tmdb.utils

import android.os.Bundle
import androidx.navigation.NavType
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import kotlinx.serialization.json.Json

class TvSeasonType : NavType<ArrayList<TvSeasonDomainModel>>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): ArrayList<TvSeasonDomainModel>? {
        return bundle.parcelableArrayList(key)
    }

    override fun parseValue(value: String): ArrayList<TvSeasonDomainModel> {
        return Json.decodeFromString<ArrayList<TvSeasonDomainModel>>(value)
    }

    override fun put(bundle: Bundle, key: String, value: ArrayList<TvSeasonDomainModel>) {
        bundle.putParcelableArrayList(key, value)
    }
}
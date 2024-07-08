package com.aldiprahasta.tmdb.utils

import android.os.Bundle
import androidx.navigation.NavType
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TvSeasonType : NavType<ArrayList<TvSeasonDomainModel>>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): ArrayList<TvSeasonDomainModel>? {
        return bundle.parcelableArrayList(key)
    }

    override fun parseValue(value: String): ArrayList<TvSeasonDomainModel> {
        val listType = object : TypeToken<List<TvSeasonDomainModel>>() {}.type
        return Gson().fromJson(value, listType)
    }

    override fun put(bundle: Bundle, key: String, value: ArrayList<TvSeasonDomainModel>) {
        bundle.putParcelableArrayList(key, value)
    }
}
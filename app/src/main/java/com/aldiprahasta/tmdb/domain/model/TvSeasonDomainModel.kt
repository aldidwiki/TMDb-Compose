package com.aldiprahasta.tmdb.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class TvSeasonDomainModel(
        val seasonId: Int,
        val seasonName: String,
        val seasonPosterPath: String?,
        val seasonAirDate: String,
        val seasonVoteAverage: Double,
        val totalEpisodes: Int,
        val seasonNumber: Int
) : Parcelable

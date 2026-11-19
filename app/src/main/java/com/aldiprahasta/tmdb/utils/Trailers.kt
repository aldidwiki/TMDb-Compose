package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.domain.model.VideoDomainModel

internal fun List<VideoDomainModel>.youtubeTrailers(): List<VideoDomainModel> =
        filter { video ->
            video.type.equals("Trailer", ignoreCase = true) &&
                    video.site.equals("YouTube", ignoreCase = true) &&
                    video.key.isNotBlank()
        }.distinctBy { it.key }.sortedByDescending { it.isOfficial }

package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvEpisodeItemResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonItemResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonResponse
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.ExternalIdDomainModel
import com.aldiprahasta.tmdb.domain.model.TvEpisodeDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel

fun MovieDetailResponse.mapMovieDetailResponseToMovieDetailDomainModel(): ContentDetailDomainModel {
    val usReleases = releaseDates?.results.orEmpty().firstOrNull { it.iso31661 == "US" }?.releaseDates.orEmpty()
    val theatricalRelease = usReleases.filter { it.type == 3 }.mapNotNull { it.releaseDate }.minOrNull()
            ?: usReleases.filter { it.type == 2 }.mapNotNull { it.releaseDate }.minOrNull()
            ?: usReleases.mapNotNull { it.releaseDate }.minOrNull()
    return ContentDetailDomainModel(
            id = id,
            title = title ?: "",
            posterPath = posterPath,
            releaseDate = releaseDate?.convertDate() ?: "",
            overview = overview ?: "",
            voteAverage = voteAverage ?: 0.0,
            genres = genres.convertGenreToSingleText(),
            backdropPath = backdropPath,
            casts = credits?.mapCreditResponseToCastDomainModelList(MediaType.MOVIE_TYPE)
                    ?: emptyList(),
            status = status ?: "",
            videos = video?.mapVideoResponseToVideoDomainModelList() ?: emptyList(),
            voteCount = voteCount,
            releaseYear = releaseDate.releaseYear(),
            runtimeMinutes = runtime?.takeIf { it > 0 },
            usReleaseDate = theatricalRelease?.take(10)?.convertDate(),
            directors = credits.namesForJob("Director"),
            composers = credits.namesForJob("Original Music Composer")
    )
}

fun TvDetailResponse.mapTvDetailResponseToContentDetailDomainModel(): ContentDetailDomainModel =
        ContentDetailDomainModel(
                title = name ?: "",
                posterPath = posterPath,
                releaseDate = firstAirDate?.convertDate() ?: "",
                overview = overview ?: "",
                id = id,
                voteAverage = voteAverage ?: 0.0,
                genres = genres.convertGenreToSingleText(),
                backdropPath = backdropPath,
                status = status ?: "",
                casts = credits?.mapCreditResponseToCastDomainModelList(MediaType.TV_TYPE)
                        ?: emptyList(),
                videos = videos?.mapVideoResponseToVideoDomainModelList() ?: emptyList(),
                seasons = seasons?.mapTvSeasonItemResponseToDomainModelList(),
                voteCount = voteCount?.toInt(),
                seasonCount = numberOfSeasons,
                releaseYear = firstAirDate.releaseYear(),
                creators = createdBy.mapNotNull { it.name?.takeIf(String::isNotBlank) }.distinct(),
                directors = credits.namesForJob("Director"),
                executiveProducers = credits.namesForJob("Executive Producer")
        )

fun TvSeasonResponse.mapTvSeasonResponseToDomainModel(): TvSeasonDetailDomainModel {
    return TvSeasonDetailDomainModel(
            tvSeasonDomainModel = TvSeasonDomainModel(
                    seasonId = id,
                    seasonName = name ?: "",
                    seasonPosterPath = posterPath,
                    seasonAirDate = airDate?.convertDate() ?: "",
                    seasonVoteAverage = voteAverage ?: 0.0,
                    totalEpisodes = episodes.size,
                    seasonNumber = seasonNumber ?: 0,
                    airDateIso = airDate
            ),
            tvEpisodeList = episodes.mapTvEpisodeItemResponseToDomainModelList().sortedBy { it.episodeNumber }
    )
}

private fun CreditResponse?.namesForJob(job: String): List<String> = this?.crew.orEmpty()
        .filter { member -> member.job == job || member.jobs.any { it.job == job } }
        .mapNotNull { it.name?.takeIf(String::isNotBlank) }
        .distinct()

internal fun ExternalIdResponse.mapExternalIdResponseToExternalIdDomainModel(): ExternalIdDomainModel {
    return ExternalIdDomainModel(
            instragramId = instagramId,
            facebookId = facebookId,
            imdbId = imdbId,
            twitterId = twitterId
    )
}

private fun List<TvSeasonItemResponse>.mapTvSeasonItemResponseToDomainModelList(): List<TvSeasonDomainModel> {
    return this.filter { it.seasonNumber != null }.map { item ->
        TvSeasonDomainModel(
                seasonId = item.id,
                seasonName = item.name ?: "",
                seasonPosterPath = item.posterPath,
                seasonAirDate = item.airDate?.convertDate() ?: "",
                seasonVoteAverage = item.voteAverage ?: 0.0,
                totalEpisodes = item.episodeCount ?: 0,
                seasonNumber = checkNotNull(item.seasonNumber),
                airDateIso = item.airDate
        )
    }
}

private fun List<TvEpisodeItemResponse>.mapTvEpisodeItemResponseToDomainModelList(): List<TvEpisodeDomainModel> {
    return this.map { item ->
        TvEpisodeDomainModel(
                id = item.id,
                name = item.name ?: "",
                stillPath = item.stillPath,
                runtime = item.runtime ?: 0,
                overview = item.overview ?: "",
                voteAverage = item.voteAverage ?: 0.0,
                episodeNumber = item.episodeNumber ?: 0,
                airDate = item.airDate?.convertDate() ?: ""
        )
    }
}

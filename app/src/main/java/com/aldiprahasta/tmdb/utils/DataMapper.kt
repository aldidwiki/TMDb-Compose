package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.local.entity.FavoriteEntity
import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ExploreResultResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ExternalIdResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.ImageResponse
import com.aldiprahasta.tmdb.data.source.remote.response.SimilarResponse
import com.aldiprahasta.tmdb.data.source.remote.response.VideoResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponseModel
import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.data.source.remote.response.search.SearchResponseModel
import com.aldiprahasta.tmdb.data.source.remote.response.tv.NetworksItemResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvEpisodeItemResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvResponseModel
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonItemResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonResponse
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.ExploreItem
import com.aldiprahasta.tmdb.domain.model.ExternalIdDomainModel
import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.model.NetworkDomainModel
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.domain.model.SimilarContent
import com.aldiprahasta.tmdb.domain.model.SimilarPage
import com.aldiprahasta.tmdb.domain.model.TvDomainModel
import com.aldiprahasta.tmdb.domain.model.TvEpisodeDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.domain.model.VideoDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageType

fun ExploreResultResponse.mapToExploreItem(mediaType: MediaType): ExploreItem = ExploreItem(
        id = id,
        mediaType = mediaType,
        title = (title ?: name ?: originalTitle ?: originalName).orEmpty(),
        imagePath = if (mediaType == MediaType.PERSON_TYPE) profilePath else posterPath,
        releaseDate = (releaseDate ?: firstAirDate)?.takeUnless(String::isBlank),
        rating = voteAverage?.takeIf { it > 0 && voteCount != 0 },
        voteCount = voteCount ?: 0,
        popularity = popularity ?: 0.0,
        genreIds = genreIds,
        overview = overview.orEmpty(),
        department = department,
        knownFor = knownFor.mapNotNull {
            when (it.mediaType) {
                Constant.MOVIE_MEDIA_TYPE -> MediaType.MOVIE_TYPE
                Constant.TV_MEDIA_TYPE -> MediaType.TV_TYPE
                else -> null
            }
        }
)

fun MovieResponseModel.mapMovieResponseToMovieDomainModel(): MovieDomainModel {
    return MovieDomainModel(
            title = title ?: "",
            posterPath = posterPath,
            releaseDate = releaseDate?.convertDate() ?: "",
            movieId = id,
            backdropPath = backdropPath,
            releaseYear = releaseDate?.take(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) },
            voteAverage = voteAverage?.takeUnless { voteCount == 0 },
            genreIds = genreIds,
            overview = overview.orEmpty()
    )
}

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
            runtime = runtime.runtimeFormat(),
            tagline = tagline ?: "",
            overview = overview ?: "",
            voteAverage = voteAverage ?: 0.0,
            genres = genres.convertGenreToSingleText(),
            backdropPath = backdropPath,
            casts = credits?.mapCreditResponseToCastDomainModelList(MediaType.MOVIE_TYPE)
                    ?: emptyList(),
            certification = usReleases.firstNotNullOfOrNull { it.certification?.takeIf(String::isNotBlank) } ?: "NR",
            budget = (budget ?: 0L).formatCurrency(),
            revenue = (revenue ?: 0L).formatCurrency(),
            originalLanguage = (originalLanguage ?: "").getLanguageDisplayName(),
            status = status ?: "",
            externalId = externalIds.mapExternalIdResponseToExternalIdDomainModel(),
            videos = video?.mapVideoResponseToVideoDomainModelList() ?: emptyList(),
            networks = null,
            type = null,
            voteCount = voteCount,
            releaseYear = releaseDate.releaseYear(),
            runtimeMinutes = runtime?.takeIf { it > 0 },
            usReleaseDate = theatricalRelease?.take(10)?.convertDate(),
            directors = credits.namesForJob("Director"),
            composers = credits.namesForJob("Original Music Composer")
    )
}

fun PersonResponse.mapPersonResponseToPersonDomainModel(): PersonDomainModel {
    return PersonDomainModel(
            id = id,
            profilePath = profilePath,
            name = name ?: "",
            birthDay = birthday?.convertDate() ?: "",
            deathDay = deathday?.convertDate() ?: "",
            gender = gender?.formatGender() ?: "-",
            biography = biography ?: "-",
            knownFor = knownForDepartment ?: "-",
            age = birthday?.let { getAge(it, deathday) } ?: "-",
            placeOfBirth = placeOfBirth ?: "-",
            externalIds = externalIds.mapExternalIdResponseToExternalIdDomainModel(),
            credits = combinedCredits?.mapCreditResponseToCastDomainModelList(MediaType.PERSON_TYPE)
                    ?: emptyList(),
            alsoKnownAs = alsoKnownAs.orEmpty().filter { it.isNotBlank() }.distinct()
    )
}

fun CreditResponse.mapCreditResponseToCastDomainModelList(mediaType: MediaType): List<CastDomainModel> {
    val cast = casts.orEmpty()
    val orderedCast = if (mediaType == MediaType.PERSON_TYPE) cast else cast.sortedBy { it.order ?: Int.MAX_VALUE }
    return orderedCast.map { castResponseModel ->
        when (mediaType) {
            MediaType.MOVIE_TYPE -> CastDomainModel(
                    name = castResponseModel.name ?: "",
                    characterName = castResponseModel.character ?: "",
                    imagePath = castResponseModel.profilePath ?: "",
                    order = castResponseModel.order ?: 0,
                    id = castResponseModel.id,
                    mediaType = null,
                    releaseDate = null,
                    totalEpisodeCount = null
            )

            MediaType.PERSON_TYPE -> CastDomainModel(
                    name = castResponseModel.title ?: castResponseModel.name ?: "",
                    characterName = castResponseModel.character?.ifEmpty { "Self" } ?: "",
                    imagePath = castResponseModel.posterPath ?: "",
                    order = castResponseModel.order ?: 0,
                    id = castResponseModel.id,
                    mediaType = castResponseModel.mediaType,
                    releaseDate = castResponseModel.releaseDate?.convertDate()
                            ?: castResponseModel.firstAirDate?.convertDate() ?: "",
                    totalEpisodeCount = castResponseModel.episodeCount,
                    genreIds = castResponseModel.genreIds,
                    popularity = castResponseModel.popularity,
                    voteAverage = castResponseModel.voteAverage?.takeUnless { castResponseModel.voteCount == 0 }
            )

            MediaType.TV_TYPE -> CastDomainModel(
                    name = castResponseModel.name ?: "",
                    characterName = castResponseModel.roles.orEmpty()
                            .firstNotNullOfOrNull { it.character?.takeIf(String::isNotBlank) }
                            ?: castResponseModel.character.orEmpty(),
                    imagePath = castResponseModel.profilePath ?: "",
                    order = castResponseModel.order ?: 0,
                    id = castResponseModel.id,
                    mediaType = null,
                    releaseDate = null,
                    totalEpisodeCount = castResponseModel.totalEpisodeCount
            )
        }
    }
}

private fun CreditResponse?.namesForJob(job: String): List<String> = this?.crew.orEmpty()
        .filter { member -> member.job == job || member.jobs.any { it.job == job } }
        .mapNotNull { it.name?.takeIf(String::isNotBlank) }
        .distinct()

private fun String?.releaseYear(): String? = this?.take(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) }

fun SimilarResponse.mapSimilarResponseToPage(): SimilarPage = SimilarPage(
        items = results.map { item ->
            SimilarContent(
                    id = item.id,
                    title = item.title ?: item.name.orEmpty(),
                    posterPath = item.posterPath,
                    releaseYear = (item.releaseDate ?: item.firstAirDate).releaseYear(),
                    voteAverage = item.voteAverage?.takeIf { it > 0 && item.voteCount != 0 }
            )
        },
        totalPages = totalPages,
        totalResults = totalResults
)

fun TvResponseModel.mapTvResponseToTvDomainModel(): TvDomainModel {
    return TvDomainModel(
            posterPath = posterPath,
            tvId = id,
            title = name ?: "",
            releaseDate = firstAirDate?.convertDate() ?: ""
    )
}

fun TvDetailResponse.mapTvDetailResponseToContentDetailDomainModel(): ContentDetailDomainModel =
        ContentDetailDomainModel(
                title = name ?: "",
                posterPath = posterPath,
                releaseDate = firstAirDate?.convertDate() ?: "",
                runtime = "",
                tagline = tagline ?: "",
                overview = overview ?: "",
                id = id,
                voteAverage = voteAverage ?: 0.0,
                genres = genres.convertGenreToSingleText(),
                certification = contentRatings.results?.firstOrNull {
                    it.code == "US"
                }?.rating ?: "NR",
                backdropPath = backdropPath,
                budget = null,
                revenue = null,
                originalLanguage = (originalLanguage ?: "").getLanguageDisplayName(),
                status = status ?: "",
                externalId = externalIds.mapExternalIdResponseToExternalIdDomainModel(),
                casts = credits?.mapCreditResponseToCastDomainModelList(MediaType.TV_TYPE)
                        ?: emptyList(),
                videos = videos?.mapVideoResponseToVideoDomainModelList() ?: emptyList(),
                networks = networks?.mapNetworkItemsToNetworkDomainModel(),
                type = type,
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

fun SearchResponseModel.mapSearchResponseToDomainModel(categoryMediaType: String?): SearchDomainModel {
    return SearchDomainModel(
            id = id,
            name = name ?: title ?: "",
            imagePath = posterPath ?: profilePath ?: "",
            releaseDate = releaseDate?.convertDate() ?: firstAirDate?.convertDate() ?: "",
            mediaType = categoryMediaType ?: mediaType.orEmpty(),
            knownFor = knownForDepartment ?: "",
            popularity = popularity ?: 0.0,
            releaseYear = (releaseDate ?: firstAirDate)?.take(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) },
            voteAverage = voteAverage?.takeUnless { voteCount == 0 },
            overview = overview.orEmpty(),
            genreIds = genreIds
    )
}

fun GenreResponse.mapGenreResponseToDomainModelList(): List<GenreDomainModel> {
    return this.genres.map {
        GenreDomainModel(
                id = it.id,
                name = it.name
        )
    }
}

fun FavoriteEntity.mapEntityToDomainModel(): FavoriteDomainModel = FavoriteDomainModel(
        favoriteId = id,
        title = name,
        releaseDate = releaseDate,
        imagePoster = imagePath,
        mediaType = mediaType,
        addedAt = addedAt,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = genres,
        seasonCount = seasonCount,
        knownFor = knownFor
)

fun FavoriteDomainModel.mapDomainModelToEntity(): FavoriteEntity = FavoriteEntity(
        id = favoriteId,
        name = title,
        releaseDate = releaseDate,
        imagePath = imagePoster,
        mediaType = mediaType,
        addedAt = addedAt,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = genres,
        seasonCount = seasonCount,
        knownFor = knownFor
)

fun ContentDetailDomainModel.mapDomainModelToEntity(mediaType: String): FavoriteEntity {
    return FavoriteEntity(
            id = id,
            name = title,
            releaseDate = releaseDate,
            imagePath = posterPath,
            mediaType = mediaType,
            addedAt = System.currentTimeMillis(),
            voteAverage = voteAverage.takeUnless { voteCount == 0 },
            voteCount = voteCount,
            genres = genres,
            seasonCount = seasonCount
    )
}

fun PersonDomainModel.mapDomainModelToEntity(): FavoriteEntity {
    return FavoriteEntity(
            id = id,
            name = name,
            releaseDate = "",
            imagePath = profilePath,
            mediaType = MediaType.PERSON_TYPE.name,
            addedAt = System.currentTimeMillis(),
            knownFor = knownFor.takeUnless { it.isBlank() || it == "-" }
    )
}

fun ImageResponse.mapImageResponseToDomainList(imageType: ImageType): List<ImageDomainModel> {
    return when (imageType) {
        ImageType.POSTER -> posters?.map { responseModel ->
            ImageDomainModel(id, responseModel.filePath)
        } ?: emptyList()

        ImageType.BACKDROP -> backdrops.orEmpty().map { responseModel ->
            val width = responseModel.width?.takeIf { it > 0 }
            val height = responseModel.height?.takeIf { it > 0 }
            val ratio = responseModel.aspectRatio?.toFloat()?.takeIf { it.isFinite() && it > 0f }
                    ?: if (width != null && height != null) width.toFloat() / height else null
            ImageDomainModel(id, responseModel.filePath, ratio)
        }

        ImageType.PROFILE -> profiles?.map { responseModel ->
            ImageDomainModel(id, responseModel.filePath, responseModel.aspectRatio?.toFloat())
        } ?: emptyList()

        else -> emptyList()
    }
}

private fun List<NetworksItemResponse>.mapNetworkItemsToNetworkDomainModel(): List<NetworkDomainModel> {
    return map { networksItem ->
        NetworkDomainModel(
                networkId = networksItem.id,
                networkLogoPath = networksItem.logoPath,
                networkName = networksItem.name ?: ""
        )
    }
}

private fun ExternalIdResponse.mapExternalIdResponseToExternalIdDomainModel(): ExternalIdDomainModel {
    return ExternalIdDomainModel(
            instragramId = instagramId,
            facebookId = facebookId,
            imdbId = imdbId,
            twitterId = twitterId
    )
}

fun VideoResponse.mapVideoResponseToVideoDomainModelList(): List<VideoDomainModel> {
    return results?.map { model ->
        VideoDomainModel(
                id = model.id,
                name = model.name ?: "",
                site = model.site ?: "",
                type = model.type ?: "",
                key = model.key ?: "",
                isOfficial = model.official ?: false
        )
    } ?: emptyList()
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

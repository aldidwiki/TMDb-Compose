package com.aldiprahasta.tmdb.utils

import com.aldiprahasta.tmdb.data.source.remote.response.ExploreResultResponse
import com.aldiprahasta.tmdb.data.source.remote.response.GenreResponse
import com.aldiprahasta.tmdb.data.source.remote.response.SimilarResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponseModel
import com.aldiprahasta.tmdb.data.source.remote.response.search.SearchResponseModel
import com.aldiprahasta.tmdb.domain.model.ExploreItem
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.domain.model.MovieDomainModel
import com.aldiprahasta.tmdb.domain.model.SearchDomainModel
import com.aldiprahasta.tmdb.domain.model.SimilarContent
import com.aldiprahasta.tmdb.domain.model.SimilarPage

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

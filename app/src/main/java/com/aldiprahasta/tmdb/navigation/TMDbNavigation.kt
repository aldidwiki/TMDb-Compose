package com.aldiprahasta.tmdb.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.MediaType

internal fun NavHostController.navigateToGenreMovieListing(genre: GenreDomainModel) {
    navigate("${GenreMovieListing.route}/${genre.id}/${Uri.encode(genre.name)}")
}

internal fun NavHostController.navigateToContentDetail(contentId: Int, contentType: String) {
    navigate("${ContentDetail.route}/$contentId/$contentType")
}

internal fun NavHostController.navigateToPersonDetail(personId: Int) {
    navigate("${PersonDetail.route}/$personId")
}

internal fun NavHostController.navigateToPersonCredit(contentId: Int, mediaType: String?) {
    val type = mediaType.toMediaType()
    if (type == MediaType.MOVIE_TYPE || type == MediaType.TV_TYPE) navigateToDetail(contentId, type)
}

internal fun NavHostController.navigateToDetail(contentId: Int, mediaType: MediaType) {
    when (mediaType) {
        MediaType.PERSON_TYPE -> navigateToPersonDetail(contentId)
        MediaType.MOVIE_TYPE, MediaType.TV_TYPE -> navigateToContentDetail(contentId, mediaType.name)
    }
}

internal fun NavHostController.navigateToDetail(contentId: Int, mediaType: String) {
    mediaType.toMediaType()?.let { navigateToDetail(contentId, it) }
}

internal fun NavHostController.navigateToPersonCredits(personId: Int, name: String) {
    navigate("${PersonCredits.route}/$personId?name=${Uri.encode(name)}")
}

private fun String?.toMediaType(): MediaType? = when (this) {
    Constant.MOVIE_MEDIA_TYPE, MediaType.MOVIE_TYPE.name -> MediaType.MOVIE_TYPE
    Constant.TV_MEDIA_TYPE, MediaType.TV_TYPE.name -> MediaType.TV_TYPE
    Constant.PERSON_MEDIA_TYPE, MediaType.PERSON_TYPE.name -> MediaType.PERSON_TYPE
    else -> null
}

internal fun NavHostController.navigateToCreditDetail(contentId: Int, mediaType: MediaType, title: String) {
    navigate("${CreditDetail.route}/$contentId/${mediaType.name}?title=${Uri.encode(title)}")
}

internal fun NavHostController.navigateToSimilarContent(contentId: Int, mediaType: MediaType, title: String, genres: String) {
    navigate("${SimilarContentListing.route}/$contentId/${mediaType.name}?title=${Uri.encode(title)}&genres=${Uri.encode(genres)}")
}

internal fun NavHostController.navigateToTvSeasonDetailScreen(tvId: Int, tvSeasonNumber: Int) {
    navigate("${TvSeasonDetail.route}/$tvId/$tvSeasonNumber")
}

internal fun NavHostController.navigateToImageGalleryScreen(contentId: Int, contentName: String, mediaType: MediaType) {
    navigate("${ImageGallery.route}/$contentId?name=${Uri.encode(contentName)}&type=${mediaType.name}")
}

fun NavHostController.navigateSingleTopTo(route: String) = navigate(route) {
    popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

@Composable
fun NavHostController.currentDestination(): NavDestination? {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    return navBackStackEntry?.destination
}

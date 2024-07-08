package com.aldiprahasta.tmdb

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.aldiprahasta.tmdb.utils.TvSeasonType

interface TMDbDestinations {
    val route: String
    val icon: ImageVector get() = Icons.Filled.Home
    val label: String get() = "Home"
}

object Movie : TMDbDestinations {
    override val route: String = "movie"
    override val icon = Icons.Default.Movie
    override val label = "Movie"
}

object Tv : TMDbDestinations {
    override val route: String = "tv_show"
    override val icon = Icons.Default.Tv
    override val label = "Tv Show"
}

object ContentDetail : TMDbDestinations {
    override val route: String = "content_detail"
    const val contentIdArg = "content_id"
    const val contentTypeArg = "content_type"
    val routeWithArgs = "$route/{$contentIdArg}/{$contentTypeArg}"
    val arguments = listOf(
            navArgument(contentIdArg) {
                type = NavType.IntType
            },
            navArgument(contentTypeArg) {
                type = NavType.StringType
            }
    )
}

object TvSeason : TMDbDestinations {
    override val route: String = "tv_season"
    const val TV_TITLE_ARG = "tv_title_arg"
    const val TV_SEASON_ARG = "tv_season_list"
    val routeWithArgs = "$route/{$TV_TITLE_ARG}/{$TV_SEASON_ARG}"
    val arguments = listOf(
            navArgument(TV_TITLE_ARG) {
                type = NavType.StringType
            },
            navArgument(TV_SEASON_ARG) {
                type = TvSeasonType()
            }
    )
}

object PersonDetail : TMDbDestinations {
    override val route: String = "person_detail"
    const val personIdArg = "person_id"
    val routeWithArgs = "$route/{$personIdArg}"
    val arguments = listOf(navArgument(personIdArg) {
        type = NavType.IntType
    })
}

object CreditDetail : TMDbDestinations {
    override val route: String = "credit_detail"
    const val contentIdArg = "content_id"
    const val contentTypeArg = "content_type"
    val routeWithArgs = "$route/{$contentIdArg}/{$contentTypeArg}"
    val arguments = listOf(
            navArgument(contentIdArg) {
                type = NavType.IntType
            },
            navArgument(contentTypeArg) {
                type = NavType.StringType
            }
    )
}
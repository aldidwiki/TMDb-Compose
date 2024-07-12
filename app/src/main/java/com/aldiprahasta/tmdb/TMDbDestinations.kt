package com.aldiprahasta.tmdb

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
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

object Search : TMDbDestinations {
    override val route: String = "search"
    override val icon: ImageVector = Icons.Default.Search
    override val label: String = "Search"
}

object Favorite : TMDbDestinations {
    override val route: String = "favorite"
    override val icon: ImageVector = Icons.Default.Favorite
    override val label: String = "Favorite"
}

object ContentDetail : TMDbDestinations {
    override val route: String = "content_detail"
    const val CONTENT_ID_ARG = "content_id"
    const val CONTENT_TYPE_ARG = "content_type"
    val routeWithArgs = "$route/{$CONTENT_ID_ARG}/{$CONTENT_TYPE_ARG}"
    val arguments = listOf(
            navArgument(CONTENT_ID_ARG) {
                type = NavType.IntType
            },
            navArgument(CONTENT_TYPE_ARG) {
                type = NavType.StringType
            }
    )
}

object TvSeason : TMDbDestinations {
    override val route: String = "tv_season"
    const val TV_ID_ARG = "tv_id_arg"
    const val TV_TITLE_ARG = "tv_title_arg"
    const val TV_SEASON_ARG = "tv_season_list"
    val routeWithArgs = "$route/{$TV_ID_ARG}/{$TV_TITLE_ARG}/{$TV_SEASON_ARG}"
    val arguments = listOf(
            navArgument(TV_ID_ARG) {
                type = NavType.IntType
            },
            navArgument(TV_TITLE_ARG) {
                type = NavType.StringType
            },
            navArgument(TV_SEASON_ARG) {
                type = TvSeasonType()
            }
    )
}

object TvSeasonDetail : TMDbDestinations {
    override val route: String = "tv_season_detail"
    const val TV_ID_ARG = "tv_id_arg"
    const val TV_SEASON_NUMBER_ARG = "tv_season_number_arg"
    val routeWithArgs = "$route/{$TV_ID_ARG}/{$TV_SEASON_NUMBER_ARG}"
    val arguments = listOf(
            navArgument(TV_ID_ARG) {
                type = NavType.IntType
            },
            navArgument(TV_SEASON_NUMBER_ARG) {
                type = NavType.IntType
            }
    )
}

object PersonDetail : TMDbDestinations {
    override val route: String = "person_detail"
    const val PERSON_ID_ARG = "person_id"
    val routeWithArgs = "$route/{$PERSON_ID_ARG}"
    val arguments = listOf(navArgument(PERSON_ID_ARG) {
        type = NavType.IntType
    })
}

object CreditDetail : TMDbDestinations {
    override val route: String = "credit_detail"
    const val CONTENT_ID_ARG = "content_id"
    const val CONTENT_TYPE_ARG = "content_type"
    val routeWithArgs = "$route/{$CONTENT_ID_ARG}/{$CONTENT_TYPE_ARG}"
    val arguments = listOf(
            navArgument(CONTENT_ID_ARG) {
                type = NavType.IntType
            },
            navArgument(CONTENT_TYPE_ARG) {
                type = NavType.StringType
            }
    )
}
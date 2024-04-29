package com.aldiprahasta.tmdb

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface TMDbDestinations {
    val route: String
}

object Movie : TMDbDestinations {
    override val route: String = "movie"
}

object MovieDetail : TMDbDestinations {
    override val route: String = "movie_detail"
    const val movieIdArg = "movie_id"
    val routeWithArgs = "$route/{$movieIdArg}"
    val arguments = listOf(navArgument(movieIdArg) {
        type = NavType.IntType
    })
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
package com.aldiprahasta.tmdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aldiprahasta.tmdb.ui.credit.CreditScreen
import com.aldiprahasta.tmdb.ui.credit.MediaType
import com.aldiprahasta.tmdb.ui.details.ContentDetailScreen
import com.aldiprahasta.tmdb.ui.movie.MovieScreen
import com.aldiprahasta.tmdb.ui.person.PersonScreen
import com.aldiprahasta.tmdb.ui.theme.TMDbTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TMDbContent()
        }
    }
}

@Composable
fun TMDbContent() {
    TMDbTheme {
        val navController = rememberNavController()

        Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                    navController = navController,
                    startDestination = Movie.route
            ) {
                composable(route = Movie.route) {
                    MovieScreen(onMovieClicked = { movieId ->
                        navController.navigateToMovieDetail(movieId)
                    })
                }

                composable(
                        route = MovieDetail.routeWithArgs,
                        arguments = MovieDetail.arguments
                ) { navBackStateEntry ->
                    val movieId = navBackStateEntry.arguments?.getInt(MovieDetail.movieIdArg) ?: 0
                    ContentDetailScreen(
                            contentId = movieId,
                            onBackPressed = {
                                navController.navigateUp()
                            },
                            onCastClicked = { personId ->
                                navController.navigateToPersonDetail(personId)
                            },
                            onViewMoreClicked = {
                                navController.navigateToCreditDetail(movieId, MediaType.MOVIE_TYPE.name)
                            }
                    )
                }

                composable(
                        route = PersonDetail.routeWithArgs,
                        arguments = PersonDetail.arguments
                ) { navBackStackEntry ->
                    val personId = navBackStackEntry.arguments?.getInt(PersonDetail.personIdArg)
                            ?: 0
                    PersonScreen(
                            personId = personId,
                            onBackPressed = {
                                navController.navigateUp()
                            },
                            onCreditClicked = { contentId, mediaType ->
                                if (mediaType == "movie") {
                                    navController.navigateToMovieDetail(contentId)
                                }
                            },
                            onViewMoreClicked = {
                                navController.navigateToCreditDetail(personId, MediaType.PERSON_TYPE.name)
                            }
                    )
                }

                composable(
                        route = CreditDetail.routeWithArgs,
                        arguments = CreditDetail.arguments
                ) { navBackStackEntry ->
                    val contentId = navBackStackEntry.arguments?.getInt(CreditDetail.contentIdArg)
                            ?: 0
                    val contentType = navBackStackEntry.arguments?.getString(CreditDetail.contentTypeArg)
                            ?: MediaType.MOVIE_TYPE.name
                    CreditScreen(
                            contentId = Pair(contentId, contentType),
                            onBackPressed = {
                                navController.navigateUp()
                            },
                            onItemClicked = { id ->
                                when (contentType) {
                                    MediaType.MOVIE_TYPE.name -> navController.navigateToPersonDetail(id) // movie credit consist people
                                    MediaType.PERSON_TYPE.name -> navController.navigateToMovieDetail(id) // reverse, person credit consist movie/tv
                                }
                            }
                    )
                }
            }
        }
    }
}

private fun NavHostController.navigateSingleTopTo(route: String) = navigate(route) {
    popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

private fun NavHostController.navigateToMovieDetail(movieId: Int) {
    navigate("${MovieDetail.route}/$movieId")
}

private fun NavHostController.navigateToPersonDetail(personId: Int) {
    navigate("${PersonDetail.route}/$personId")
}

private fun NavHostController.navigateToCreditDetail(contentId: Int, contentType: String) {
    navigate("${CreditDetail.route}/$contentId/$contentType")
}
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
import com.aldiprahasta.tmdb.ui.details.ContentDetailScreen
import com.aldiprahasta.tmdb.ui.movie.MovieScreen
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
                    ContentDetailScreen(contentId = movieId)
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
    navigateSingleTopTo("${MovieDetail.route}/$movieId")
}
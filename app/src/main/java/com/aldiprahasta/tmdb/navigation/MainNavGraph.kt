package com.aldiprahasta.tmdb.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.ui.credit.CreditScreen
import com.aldiprahasta.tmdb.ui.dashboard.DashboardScreen
import com.aldiprahasta.tmdb.ui.details.ContentDetailScreen
import com.aldiprahasta.tmdb.ui.favorite.FavoriteScreen
import com.aldiprahasta.tmdb.ui.genre.GenreMovieScreen
import com.aldiprahasta.tmdb.ui.image.ImageGalleryScreen
import com.aldiprahasta.tmdb.ui.movie.MovieScreen
import com.aldiprahasta.tmdb.ui.person.PersonScreen
import com.aldiprahasta.tmdb.ui.person.credits.PersonCreditsScreen
import com.aldiprahasta.tmdb.ui.search.SearchScreen
import com.aldiprahasta.tmdb.ui.tv.TvScreen
import com.aldiprahasta.tmdb.ui.tv.tvseason.TvSeasonDetailScreen
import com.aldiprahasta.tmdb.ui.tv.tvseason.TvSeasonScreen
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.parcelableArrayList
import kotlinx.serialization.json.Json

val navigationItem = listOf(
        Dashboard,
        Movie,
        Tv,
        Favorite,
        Search
)

private fun NavHostController.navigateToGenreMovieListing(genre: GenreDomainModel) {
    navigate("${GenreMovieListing.route}/${genre.id}/${Uri.encode(genre.name)}")
}

private fun NavHostController.navigateToContentDetail(contentId: Int, contentType: String) {
    navigate("${ContentDetail.route}/$contentId/$contentType")
}

private fun NavHostController.navigateToPersonDetail(personId: Int) {
    navigate("${PersonDetail.route}/$personId")
}

private fun NavHostController.navigateToPersonCredit(contentId: Int, mediaType: String?) {
    when (mediaType) {
        Constant.MOVIE_MEDIA_TYPE -> navigateToContentDetail(contentId, MediaType.MOVIE_TYPE.name)
        Constant.TV_MEDIA_TYPE -> navigateToContentDetail(contentId, MediaType.TV_TYPE.name)
    }
}

private fun NavHostController.navigateToCreditDetail(contentId: Int, contentType: String) {
    navigate("${CreditDetail.route}/$contentId/$contentType")
}

private fun NavHostController.navigateToTvSeasonScreen(tvId: Int, tvTitle: String, tvSeasonList: List<TvSeasonDomainModel>) {
    val tvSeasonListJson = Uri.encode(Json.encodeToString(value = tvSeasonList))
    navigate("${TvSeason.route}/$tvId/$tvTitle/$tvSeasonListJson")
}

private fun NavHostController.navigateToTvSeasonDetailScreen(tvId: Int, tvSeasonNumber: Int) {
    navigate("${TvSeasonDetail.route}/$tvId/$tvSeasonNumber")
}

private fun NavHostController.navigateToImageGalleryScreen(contentId: Int, contentName: String) {
    navigate("${ImageGallery.route}/$contentId?name=${Uri.encode(contentName)}")
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

private val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.(Boolean) -> EnterTransition = { isPop ->
    fadeIn(
            animationSpec = tween(delayMillis = 200, easing = LinearEasing)
    ) + slideIntoContainer(
            animationSpec = tween(delayMillis = 100, easing = EaseIn),
            towards = if (isPop) AnimatedContentTransitionScope.SlideDirection.End
            else AnimatedContentTransitionScope.SlideDirection.Start
    )
}

private val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.(Boolean) -> ExitTransition = { isPop ->
    fadeOut(
            animationSpec = tween(delayMillis = 200, easing = LinearEasing)
    ) + slideOutOfContainer(
            animationSpec = tween(delayMillis = 100, easing = EaseOut),
            towards = if (isPop) AnimatedContentTransitionScope.SlideDirection.Start
            else AnimatedContentTransitionScope.SlideDirection.End
    )
}

private val dashboardItemEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(animationSpec = tween(500, easing = LinearEasing))
}

private val dashboardItemExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(animationSpec = tween(500, easing = LinearEasing))
}

@Composable
fun TMDbNavHostController(
        navController: NavHostController,
        modifier: Modifier = Modifier
) {
    NavHost(
            navController = navController,
            startDestination = Dashboard.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            modifier = modifier
    ) {
        composable(
                route = Dashboard.route,
                enterTransition = dashboardItemEnterTransition,
                exitTransition = dashboardItemExitTransition
        ) {
            DashboardScreen(
                    onMovieClicked = { movieId ->
                        navController.navigateToContentDetail(movieId, MediaType.MOVIE_TYPE.name)
                    },
                    onGenreClicked = navController::navigateToGenreMovieListing
            )
        }

        composable(
                route = GenreMovieListing.routeWithArgs,
                arguments = GenreMovieListing.arguments,
                enterTransition = { enterTransition(false) },
                exitTransition = { exitTransition(false) },
                popEnterTransition = { enterTransition(true) },
                popExitTransition = { exitTransition(true) }
        ) { navBackStackEntry ->
            GenreMovieScreen(
                    genreName = navBackStackEntry.arguments?.getString(GenreMovieListing.GENRE_NAME_ARG).orEmpty(),
                    onBackPressed = { navController.navigateUp() },
                    onMovieClicked = { movieId ->
                        navController.navigateToContentDetail(movieId, MediaType.MOVIE_TYPE.name)
                    }
            )
        }

        composable(
                route = Movie.route,
                enterTransition = dashboardItemEnterTransition,
                exitTransition = dashboardItemExitTransition
        ) {
            MovieScreen(onMovieClicked = { movieId ->
                navController.navigateToContentDetail(movieId, MediaType.MOVIE_TYPE.name)
            })
        }

        composable(
                route = Tv.route,
                enterTransition = dashboardItemEnterTransition,
                exitTransition = dashboardItemExitTransition
        ) {
            TvScreen(onItemClicked = { tvId ->
                navController.navigateToContentDetail(tvId, MediaType.TV_TYPE.name)
            })
        }

        composable(
                route = Favorite.route,
                enterTransition = dashboardItemEnterTransition,
                exitTransition = dashboardItemExitTransition
        ) {
            FavoriteScreen(
                    onBackPressed = { navController.navigateUp() },
                    onItemClicked = { contentId, mediaType ->
                        if (mediaType == MediaType.PERSON_TYPE.name) {
                            navController.navigateToPersonDetail(contentId)
                        } else {
                            navController.navigateToContentDetail(contentId, mediaType)
                        }
                    }
            )
        }

        composable(
                route = Search.route,
                enterTransition = dashboardItemEnterTransition,
                exitTransition = dashboardItemExitTransition
        ) {
            SearchScreen(
                    onBackPressed = {
                        navController.navigateUp()
                    },
                    onItemClicked = { contentId, mediaType ->
                        when (mediaType) {
                            Constant.MOVIE_MEDIA_TYPE -> navController.navigateToContentDetail(contentId, MediaType.MOVIE_TYPE.name)

                            Constant.TV_MEDIA_TYPE -> navController.navigateToContentDetail(contentId, MediaType.TV_TYPE.name)

                            Constant.PERSON_MEDIA_TYPE -> navController.navigateToPersonDetail(contentId)
                        }
                    })
        }

        composable(
                route = ImageGallery.routeWithArgs,
                arguments = ImageGallery.arguments,
                enterTransition = { enterTransition(false) },
                exitTransition = { exitTransition(false) },
                popEnterTransition = { enterTransition(true) },
                popExitTransition = { exitTransition(true) }
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.apply {
                val contentName = getString(ImageGallery.CONTENT_NAME_ARG, "")

                ImageGalleryScreen(
                        contentName = contentName,
                        onBackPressed = {
                            navController.navigateUp()
                        }
                )
            }
        }

        composable(
                route = ContentDetail.routeWithArgs,
                arguments = ContentDetail.arguments,
                enterTransition = { enterTransition(false) },
                exitTransition = { exitTransition(false) },
                popEnterTransition = { enterTransition(true) },
                popExitTransition = { exitTransition(true) }
        ) { navBackStateEntry ->
            navBackStateEntry.arguments?.apply {
                val contentId = getInt(ContentDetail.CONTENT_ID_ARG, 0)
                val contentType = getString(ContentDetail.CONTENT_TYPE_ARG, "")

                ContentDetailScreen(
                        contentParam = Pair(contentId, contentType),
                        onBackPressed = {
                            navController.navigateUp()
                        },
                        onCastClicked = { personId ->
                            navController.navigateToPersonDetail(personId)
                        },
                        onViewMoreClicked = {
                            navController.navigateToCreditDetail(contentId, contentType)
                        },
                        onAllSeasonClicked = { tvTitle, tvSeasonList ->
                            navController.navigateToTvSeasonScreen(contentId, tvTitle, tvSeasonList)
                        }
                )
            }
        }

        composable(
                route = PersonDetail.routeWithArgs,
                arguments = PersonDetail.arguments,
                enterTransition = { enterTransition(false) },
                exitTransition = { exitTransition(false) },
                popEnterTransition = { enterTransition(true) },
                popExitTransition = { exitTransition(true) }
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.apply {
                val personId = getInt(PersonDetail.PERSON_ID_ARG, 0)

                PersonScreen(
                        personId = personId,
                        onBackPressed = {
                            navController.navigateUp()
                        },
                        onCreditClicked = navController::navigateToPersonCredit,
                        onViewMoreClicked = { name ->
                            navController.navigate("${PersonCredits.route}/$personId?name=${Uri.encode(name)}")
                        },
                        onPersonImageClicked = { personId, personName ->
                            navController.navigateToImageGalleryScreen(personId, personName)
                        }
                )
            }
        }

        composable(
                route = PersonCredits.routeWithArgs,
                arguments = PersonCredits.arguments,
                enterTransition = { enterTransition(false) },
                exitTransition = { exitTransition(false) },
                popEnterTransition = { enterTransition(true) },
                popExitTransition = { exitTransition(true) }
        ) { navBackStackEntry ->
            PersonCreditsScreen(
                    personName = navBackStackEntry.arguments?.getString(PersonCredits.PERSON_NAME_ARG).orEmpty(),
                    onBackPressed = { navController.navigateUp() },
                    onCreditClicked = navController::navigateToPersonCredit
            )
        }

        composable(
                route = CreditDetail.routeWithArgs,
                arguments = CreditDetail.arguments,
                enterTransition = { enterTransition(false) },
                exitTransition = { exitTransition(false) },
                popEnterTransition = { enterTransition(true) },
                popExitTransition = { exitTransition(true) }
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.apply {
                val contentId = getInt(CreditDetail.CONTENT_ID_ARG)
                val contentType = getString(CreditDetail.CONTENT_TYPE_ARG, MediaType.MOVIE_TYPE.name)

                CreditScreen(
                        creditParam = Pair(contentId, contentType),
                        onBackPressed = {
                            navController.navigateUp()
                        },
                        onItemClicked = { id, mediaType ->
                            when (contentType) {
                                MediaType.MOVIE_TYPE.name, MediaType.TV_TYPE.name -> navController.navigateToPersonDetail(id) // movie credit consist people
                                MediaType.PERSON_TYPE.name -> when (mediaType) {
                                    Constant.MOVIE_MEDIA_TYPE -> navController.navigateToContentDetail(id, MediaType.MOVIE_TYPE.name)
                                    Constant.TV_MEDIA_TYPE -> navController.navigateToContentDetail(id, MediaType.TV_TYPE.name)
                                } // reverse, person credit consist movie/tv
                            }
                        }
                )
            }
        }

        composable(
                route = TvSeason.routeWithArgs,
                arguments = TvSeason.arguments,
                enterTransition = { enterTransition(false) },
                exitTransition = { exitTransition(false) },
                popEnterTransition = { enterTransition(true) },
                popExitTransition = { exitTransition(true) }
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.apply {
                val tvTitle = getString(TvSeason.TV_TITLE_ARG, "")
                val tvSeasonList = parcelableArrayList<TvSeasonDomainModel>(TvSeason.TV_SEASON_ARG)
                        ?: emptyList()
                val tvId = getInt(TvSeason.TV_ID_ARG, 0)

                TvSeasonScreen(
                        tvTitle = tvTitle,
                        tvSeasonList = tvSeasonList,
                        onBackPressed = {
                            navController.navigateUp()
                        },
                        onItemClicked = { tvSeasonNumber ->
                            navController.navigateToTvSeasonDetailScreen(tvId, tvSeasonNumber)
                        }
                )
            }
        }

        composable(
                route = TvSeasonDetail.routeWithArgs,
                arguments = TvSeasonDetail.arguments,
                enterTransition = { enterTransition(false) },
                exitTransition = { exitTransition(false) },
                popEnterTransition = { enterTransition(true) },
                popExitTransition = { exitTransition(true) }
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.apply {
                val tvId = getInt(TvSeasonDetail.TV_ID_ARG, 0)
                val tvSeasonNumber = getInt(TvSeasonDetail.TV_SEASON_NUMBER_ARG, 0)

                TvSeasonDetailScreen(
                        tvId = tvId,
                        tvSeasonNumber = tvSeasonNumber,
                        onBackPressed = {
                            navController.navigateUp()
                        }
                )
            }
        }
    }
}

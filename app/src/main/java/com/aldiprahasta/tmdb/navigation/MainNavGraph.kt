package com.aldiprahasta.tmdb.navigation

import androidx.compose.animation.AnimatedContentScope
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
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aldiprahasta.tmdb.ui.credit.CreditScreen
import com.aldiprahasta.tmdb.ui.dashboard.DashboardScreen
import com.aldiprahasta.tmdb.ui.details.ContentDetailScreen
import com.aldiprahasta.tmdb.ui.details.similar.SimilarContentScreen
import com.aldiprahasta.tmdb.ui.explore.ExploreScreen
import com.aldiprahasta.tmdb.ui.favorite.FavoriteScreen
import com.aldiprahasta.tmdb.ui.genre.GenreMovieScreen
import com.aldiprahasta.tmdb.ui.image.ImageGalleryScreen
import com.aldiprahasta.tmdb.ui.movie.MovieScreen
import com.aldiprahasta.tmdb.ui.person.PersonScreen
import com.aldiprahasta.tmdb.ui.person.credits.PersonCreditsScreen
import com.aldiprahasta.tmdb.ui.search.SearchScreen
import com.aldiprahasta.tmdb.ui.tv.TvScreen
import com.aldiprahasta.tmdb.ui.tv.tvseason.TvSeasonDetailScreen
import com.aldiprahasta.tmdb.utils.MediaType

val navigationItem = listOf(
        Dashboard,
        Explore,
        Movie,
        Tv,
        Favorite,
        Search
)

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

private fun NavGraphBuilder.detailDestination(
        route: String,
        arguments: List<NamedNavArgument>,
        content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
            route = route,
            arguments = arguments,
            enterTransition = { enterTransition(false) },
            exitTransition = { exitTransition(false) },
            popEnterTransition = { enterTransition(true) },
            popExitTransition = { exitTransition(true) },
            content = content
    )
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
                route = Explore.route,
                enterTransition = dashboardItemEnterTransition,
                exitTransition = dashboardItemExitTransition
        ) {
            ExploreScreen(onItemClicked = navController::navigateToDetail)
        }

        detailDestination(GenreMovieListing.routeWithArgs, GenreMovieListing.arguments) { navBackStackEntry ->
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
            FavoriteScreen(onItemClicked = navController::navigateToDetail)
        }

        composable(
                route = Search.route,
                enterTransition = dashboardItemEnterTransition,
                exitTransition = dashboardItemExitTransition
        ) {
            SearchScreen(onItemClicked = navController::navigateToDetail)
        }

        detailDestination(ImageGallery.routeWithArgs, ImageGallery.arguments) { navBackStackEntry ->
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

        detailDestination(ContentDetail.routeWithArgs, ContentDetail.arguments) { navBackStateEntry ->
            navBackStateEntry.arguments?.apply {
                val contentId = getInt(ContentDetail.CONTENT_ID_ARG, 0)
                val mediaType = if (getString(ContentDetail.CONTENT_TYPE_ARG) == MediaType.TV_TYPE.name) MediaType.TV_TYPE
                else MediaType.MOVIE_TYPE

                ContentDetailScreen(
                        contentId = contentId,
                        mediaType = mediaType,
                        onBackPressed = { navController.navigateUp() },
                        onCastClicked = navController::navigateToPersonDetail,
                        onFullCastClicked = { title -> navController.navigateToCreditDetail(contentId, mediaType, title) },
                        onSimilarClicked = { id -> navController.navigateToContentDetail(id, mediaType.name) },
                        onAllSimilarClicked = { title, genres ->
                            navController.navigateToSimilarContent(contentId, mediaType, title, genres)
                        },
                        onAllEpisodesClicked = { season -> navController.navigateToTvSeasonDetailScreen(contentId, season) },
                        onOpenPhotos = { title -> navController.navigateToImageGalleryScreen(contentId, title, mediaType) }
                )
            }
        }

        detailDestination(PersonDetail.routeWithArgs, PersonDetail.arguments) { navBackStackEntry ->
            navBackStackEntry.arguments?.apply {
                val personId = getInt(PersonDetail.PERSON_ID_ARG, 0)

                PersonScreen(
                        personId = personId,
                        onBackPressed = {
                            navController.navigateUp()
                        },
                        onCreditClicked = navController::navigateToPersonCredit,
                        onViewMoreClicked = { name ->
                            navController.navigateToPersonCredits(personId, name)
                        },
                        onPersonImageClicked = { personId, personName ->
                            navController.navigateToImageGalleryScreen(personId, personName, MediaType.PERSON_TYPE)
                        }
                )
            }
        }

        detailDestination(PersonCredits.routeWithArgs, PersonCredits.arguments) { navBackStackEntry ->
            PersonCreditsScreen(
                    personName = navBackStackEntry.arguments?.getString(PersonCredits.PERSON_NAME_ARG).orEmpty(),
                    onBackPressed = { navController.navigateUp() },
                    onCreditClicked = navController::navigateToPersonCredit
            )
        }

        detailDestination(CreditDetail.routeWithArgs, CreditDetail.arguments) { navBackStackEntry ->
            navBackStackEntry.arguments?.apply {
                val contentType = getString(CreditDetail.CONTENT_TYPE_ARG, MediaType.MOVIE_TYPE.name)

                CreditScreen(
                        contentTitle = getString(CreditDetail.CONTENT_TITLE_ARG, ""),
                        isTv = contentType == MediaType.TV_TYPE.name,
                        onBackPressed = { navController.navigateUp() },
                        onPersonClicked = navController::navigateToPersonDetail
                )
            }
        }

        detailDestination(SimilarContentListing.routeWithArgs, SimilarContentListing.arguments) { entry ->
            val args = checkNotNull(entry.arguments)
            val contentType = args.getString(SimilarContentListing.CONTENT_TYPE_ARG, MediaType.MOVIE_TYPE.name)
            SimilarContentScreen(
                    isTv = contentType == MediaType.TV_TYPE.name,
                    contentTitle = args.getString(SimilarContentListing.CONTENT_TITLE_ARG, ""),
                    genres = args.getString(SimilarContentListing.GENRES_ARG, ""),
                    onBackPressed = { navController.navigateUp() },
                    onItemClicked = { id -> navController.navigateToContentDetail(id, contentType) }
            )
        }

        detailDestination(TvSeasonDetail.routeWithArgs, TvSeasonDetail.arguments) { navBackStackEntry ->
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

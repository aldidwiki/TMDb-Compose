package com.aldiprahasta.tmdb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.aldiprahasta.tmdb.ui.person.PersonScreen
import com.aldiprahasta.tmdb.ui.person.credits.PersonCreditsScreen
import com.aldiprahasta.tmdb.ui.search.SearchScreen
import com.aldiprahasta.tmdb.ui.tv.tvseason.TvSeasonDetailScreen
import com.aldiprahasta.tmdb.utils.MediaType

val navigationItem = listOf(
        Dashboard,
        Explore,
        Favorite,
        Search
)

@Composable
fun TMDbNavHostController(
        navController: NavHostController,
        modifier: Modifier = Modifier
) {
    NavHost(
            navController = navController,
            startDestination = Dashboard.route,
            enterTransition = { enterPage() },
            exitTransition = { exitPage() },
            popEnterTransition = { revealPreviousPage() },
            popExitTransition = { leavePage() },
            modifier = modifier
    ) {
        composable(route = Dashboard.route) {
            DashboardScreen(
                    onMovieClicked = { movieId ->
                        navController.navigateToContentDetail(movieId, MediaType.MOVIE_TYPE.name)
                    },
                    onGenreClicked = navController::navigateToGenreMovieListing
            )
        }

        composable(route = Explore.route) {
            ExploreScreen(onItemClicked = navController::navigateToDetail)
        }

        composable(route = GenreMovieListing.routeWithArgs, arguments = GenreMovieListing.arguments) { navBackStackEntry ->
            GenreMovieScreen(
                    genreName = navBackStackEntry.arguments?.getString(GenreMovieListing.GENRE_NAME_ARG).orEmpty(),
                    onBackPressed = { navController.navigateUp() },
                    onMovieClicked = { movieId ->
                        navController.navigateToContentDetail(movieId, MediaType.MOVIE_TYPE.name)
                    }
            )
        }

        composable(route = Favorite.route) {
            FavoriteScreen(onItemClicked = navController::navigateToDetail)
        }

        composable(route = Search.route) {
            SearchScreen(onItemClicked = navController::navigateToDetail)
        }

        composable(route = ImageGallery.routeWithArgs, arguments = ImageGallery.arguments) { navBackStackEntry ->
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

        composable(route = ContentDetail.routeWithArgs, arguments = ContentDetail.arguments) { navBackStateEntry ->
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

        composable(route = PersonDetail.routeWithArgs, arguments = PersonDetail.arguments) { navBackStackEntry ->
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

        composable(route = PersonCredits.routeWithArgs, arguments = PersonCredits.arguments) { navBackStackEntry ->
            PersonCreditsScreen(
                    personName = navBackStackEntry.arguments?.getString(PersonCredits.PERSON_NAME_ARG).orEmpty(),
                    onBackPressed = { navController.navigateUp() },
                    onCreditClicked = navController::navigateToPersonCredit
            )
        }

        composable(route = CreditDetail.routeWithArgs, arguments = CreditDetail.arguments) { navBackStackEntry ->
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

        composable(route = SimilarContentListing.routeWithArgs, arguments = SimilarContentListing.arguments) { entry ->
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

        composable(route = TvSeasonDetail.routeWithArgs, arguments = TvSeasonDetail.arguments) { navBackStackEntry ->
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

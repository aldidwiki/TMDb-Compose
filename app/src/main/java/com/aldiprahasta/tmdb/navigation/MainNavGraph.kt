package com.aldiprahasta.tmdb.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.ui.credit.CreditScreen
import com.aldiprahasta.tmdb.ui.details.ContentDetailScreen
import com.aldiprahasta.tmdb.ui.favorite.FavoriteScreen
import com.aldiprahasta.tmdb.ui.image.ImageGalleryScreen
import com.aldiprahasta.tmdb.ui.movie.MovieScreen
import com.aldiprahasta.tmdb.ui.person.PersonScreen
import com.aldiprahasta.tmdb.ui.search.SearchScreen
import com.aldiprahasta.tmdb.ui.tv.TvScreen
import com.aldiprahasta.tmdb.ui.tv.tvseason.TvSeasonDetailScreen
import com.aldiprahasta.tmdb.ui.tv.tvseason.TvSeasonScreen
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.parcelableArrayList
import kotlinx.serialization.json.Json

val navigationItem = listOf(
        Movie,
        Tv,
        Favorite,
        Search
)

private fun NavHostController.navigateToContentDetail(contentId: Int, contentType: String) {
    navigate("${ContentDetail.route}/$contentId/$contentType")
}

private fun NavHostController.navigateToPersonDetail(personId: Int) {
    navigate("${PersonDetail.route}/$personId")
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
    navigate("${ImageGallery.route}/$contentId/$contentName")
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

@Composable
fun TMDbNavHostController(
        navController: NavHostController,
        modifier: Modifier = Modifier
) {
    NavHost(
            navController = navController,
            startDestination = Movie.route,
            modifier = modifier
    ) {
        composable(route = Movie.route) {
            MovieScreen(onMovieClicked = { movieId ->
                navController.navigateToContentDetail(movieId, MediaType.MOVIE_TYPE.name)
            })
        }

        composable(route = Tv.route) {
            TvScreen(onItemClicked = { tvId ->
                navController.navigateToContentDetail(tvId, MediaType.TV_TYPE.name)
            })
        }

        composable(route = Favorite.route) {
            FavoriteScreen(onItemClicked = { contentId, mediaType ->
                if (mediaType == MediaType.PERSON_TYPE.name) {
                    navController.navigateToPersonDetail(contentId)
                } else {
                    navController.navigateToContentDetail(contentId, mediaType)
                }
            })
        }

        composable(route = Search.route) {
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
                arguments = ImageGallery.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.apply {
                val contentId = getInt(ImageGallery.CONTENT_ID_ARG, 0)
                val contentName = getString(ImageGallery.CONTENT_NAME_ARG, "")

                ImageGalleryScreen(
                        contentId = contentId,
                        contentName = contentName,
                        onBackPressed = {
                            navController.navigateUp()
                        }
                )
            }
        }

        composable(
                route = ContentDetail.routeWithArgs,
                arguments = ContentDetail.arguments
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
                arguments = PersonDetail.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.apply {
                val personId = getInt(PersonDetail.PERSON_ID_ARG, 0)

                PersonScreen(
                        personId = personId,
                        onBackPressed = {
                            navController.navigateUp()
                        },
                        onCreditClicked = { contentId, mediaType ->
                            if (mediaType == Constant.MOVIE_MEDIA_TYPE) {
                                navController.navigateToContentDetail(contentId, MediaType.MOVIE_TYPE.name)
                            } else if (mediaType == Constant.TV_MEDIA_TYPE) {
                                navController.navigateToContentDetail(contentId, MediaType.TV_TYPE.name)
                            }
                        },
                        onViewMoreClicked = {
                            navController.navigateToCreditDetail(personId, MediaType.PERSON_TYPE.name)
                        },
                        onPersonImageClicked = { personId, personName ->
                            navController.navigateToImageGalleryScreen(personId, personName)
                        }
                )
            }
        }

        composable(
                route = CreditDetail.routeWithArgs,
                arguments = CreditDetail.arguments
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
                arguments = TvSeason.arguments
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
                arguments = TvSeasonDetail.arguments
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
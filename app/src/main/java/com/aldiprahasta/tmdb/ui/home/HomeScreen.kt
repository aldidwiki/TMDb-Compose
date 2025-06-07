package com.aldiprahasta.tmdb.ui.home

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aldiprahasta.tmdb.ContentDetail
import com.aldiprahasta.tmdb.CreditDetail
import com.aldiprahasta.tmdb.Favorite
import com.aldiprahasta.tmdb.Movie
import com.aldiprahasta.tmdb.PersonDetail
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.Search
import com.aldiprahasta.tmdb.Tv
import com.aldiprahasta.tmdb.TvSeason
import com.aldiprahasta.tmdb.TvSeasonDetail
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.ui.credit.CreditScreen
import com.aldiprahasta.tmdb.ui.details.ContentDetailScreen
import com.aldiprahasta.tmdb.ui.favorite.FavoriteScreen
import com.aldiprahasta.tmdb.ui.movie.MovieScreen
import com.aldiprahasta.tmdb.ui.person.PersonScreen
import com.aldiprahasta.tmdb.ui.search.SearchScreen
import com.aldiprahasta.tmdb.ui.theme.TMDBSecondaryColor
import com.aldiprahasta.tmdb.ui.tv.TvScreen
import com.aldiprahasta.tmdb.ui.tv.TvSeasonDetailScreen
import com.aldiprahasta.tmdb.ui.tv.TvSeasonScreen
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.parcelableArrayList
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var isVisibleBar by remember { mutableStateOf(true) }

    Scaffold(
            modifier = modifier,
            topBar = {
                AnimatedVisibility(
                        visible = isVisibleBar,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                ) {
                    TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = Color.White,
                            ),
                            title = {
                                Image(
                                        painter = painterResource(id = R.drawable.tmdb_logo_long),
                                        contentDescription = null,
                                        modifier = Modifier.size(120.dp)
                                )
                            },
                    )
                }
            },
            bottomBar = {
                AnimatedVisibility(
                        visible = isVisibleBar,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                ) {
                    TMDbBottomNavigation(navController)
                }
            }
    ) { innerPadding ->
        TMDbNavHostController(
                navController = navController,
                modifier = Modifier.padding(
                        top = if (isVisibleBar) innerPadding.calculateTopPadding() else 0.dp,
                        bottom = if (isVisibleBar) innerPadding.calculateBottomPadding() else 0.dp
                )
        )
    }

    isVisibleBar = navigationItem().find {
        it.route == navController.currentDestination()?.route
    } != null
}

@Composable
fun TMDbBottomNavigation(
        navController: NavHostController,
        modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        navigationItem().forEach { tmDbDestinations ->
            NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(indicatorColor = TMDBSecondaryColor),
                    selected = tmDbDestinations.route == navController.currentDestination()?.route,
                    onClick = {
                        navController.navigateSingleTopTo(tmDbDestinations.route)
                    },
                    icon = {
                        Icon(imageVector = tmDbDestinations.icon, contentDescription = null)
                    },
                    label = {
                        Text(text = tmDbDestinations.label)
                    }
            )
        }
    }
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
                route = ContentDetail.routeWithArgs,
                arguments = ContentDetail.arguments
        ) { navBackStateEntry ->
            val contentId = navBackStateEntry.arguments?.getInt(ContentDetail.CONTENT_ID_ARG) ?: 0
            val contentType = navBackStateEntry.arguments?.getString(ContentDetail.CONTENT_TYPE_ARG)
                    ?: ""

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

        composable(
                route = PersonDetail.routeWithArgs,
                arguments = PersonDetail.arguments
        ) { navBackStackEntry ->
            val personId = navBackStackEntry.arguments?.getInt(PersonDetail.PERSON_ID_ARG)
                    ?: 0
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
                    }
            )
        }

        composable(
                route = CreditDetail.routeWithArgs,
                arguments = CreditDetail.arguments
        ) { navBackStackEntry ->
            val contentId = navBackStackEntry.arguments?.getInt(CreditDetail.CONTENT_ID_ARG)
                    ?: 0
            val contentType = navBackStackEntry.arguments?.getString(CreditDetail.CONTENT_TYPE_ARG)
                    ?: MediaType.MOVIE_TYPE.name
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

        composable(
                route = TvSeason.routeWithArgs,
                arguments = TvSeason.arguments
        ) { navBackStackEntry ->
            val tvTitle = navBackStackEntry.arguments?.getString(TvSeason.TV_TITLE_ARG) ?: ""
            val tvSeasonList = navBackStackEntry.arguments?.parcelableArrayList<TvSeasonDomainModel>(TvSeason.TV_SEASON_ARG)
                    ?: emptyList()
            val tvId = navBackStackEntry.arguments?.getInt(TvSeason.TV_ID_ARG, 0) ?: 0

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

        composable(
                route = TvSeasonDetail.routeWithArgs,
                arguments = TvSeasonDetail.arguments
        ) { navBackStackEntry ->
            val tvId = navBackStackEntry.arguments?.getInt(TvSeasonDetail.TV_ID_ARG, 0) ?: 0
            val tvSeasonNumber = navBackStackEntry.arguments?.getInt(TvSeasonDetail.TV_SEASON_NUMBER_ARG, 0)
                    ?: 0

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

private fun navigationItem() = listOf(
        Movie,
        Tv,
        Favorite,
        Search
)

@Composable
private fun NavHostController.currentDestination(): NavDestination? {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    return navBackStackEntry?.destination
}

private fun NavHostController.navigateSingleTopTo(route: String) = navigate(route) {
    popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

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
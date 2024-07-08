package com.aldiprahasta.tmdb.ui.home

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.aldiprahasta.tmdb.Movie
import com.aldiprahasta.tmdb.PersonDetail
import com.aldiprahasta.tmdb.Tv
import com.aldiprahasta.tmdb.TvSeason
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.ui.credit.CreditScreen
import com.aldiprahasta.tmdb.ui.details.ContentDetailScreen
import com.aldiprahasta.tmdb.ui.movie.MovieScreen
import com.aldiprahasta.tmdb.ui.person.PersonScreen
import com.aldiprahasta.tmdb.ui.tv.TvScreen
import com.aldiprahasta.tmdb.ui.tv.TvSeasonScreen
import com.aldiprahasta.tmdb.utils.MediaType
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val navController = rememberNavController()
    var isVisibleBar by remember { mutableStateOf(true) }

    Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                                Text(text = "TMDb")
                            },
                            scrollBehavior = scrollBehavior
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
                    selected = tmDbDestinations.route == navController.currentDestination()?.route,
                    onClick = {
                        navController.navigate(tmDbDestinations.route)
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

        composable(
                route = ContentDetail.routeWithArgs,
                arguments = ContentDetail.arguments
        ) { navBackStateEntry ->
            val contentId = navBackStateEntry.arguments?.getInt(ContentDetail.contentIdArg) ?: 0
            val contentType = navBackStateEntry.arguments?.getString(ContentDetail.contentTypeArg)
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
                        navController.navigateToTvSeasonScreen(tvTitle, tvSeasonList)
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
                            navController.navigateToContentDetail(contentId, MediaType.MOVIE_TYPE.name)
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
                            MediaType.MOVIE_TYPE.name, MediaType.TV_TYPE.name -> navController.navigateToPersonDetail(id) // movie credit consist people
                            MediaType.PERSON_TYPE.name -> navController.navigateToContentDetail(id, MediaType.MOVIE_TYPE.name) // reverse, person credit consist movie/tv
                        }
                    }
            )
        }

        composable(
                route = TvSeason.routeWithArgs,
                arguments = TvSeason.arguments
        ) { navBackStackEntry ->
            val tvTitle = navBackStackEntry.arguments?.getString(TvSeason.TV_TITLE_ARG) ?: ""
            val tvSeasonList = navBackStackEntry.arguments?.getParcelableArrayList<TvSeasonDomainModel>(TvSeason.TV_SEASON_ARG)
                    ?: emptyList()

            TvSeasonScreen(
                    tvTitle = tvTitle,
                    tvSeasonList = tvSeasonList,
                    onBackPressed = {
                        navController.navigateUp()
                    }
            )
        }
    }
}

private fun navigationItem() = listOf(
        Movie,
        Tv
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

private fun NavHostController.navigateToTvSeasonScreen(tvTitle: String, tvSeasonList: List<TvSeasonDomainModel>) {
    val tvSeasonListJson = Uri.encode(Gson().toJson(tvSeasonList))
    navigate("${TvSeason.route}/$tvTitle/$tvSeasonListJson")
}
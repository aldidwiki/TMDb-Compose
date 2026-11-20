package com.aldiprahasta.tmdb.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.compose.rememberNavController
import com.aldiprahasta.tmdb.navigation.Dashboard
import com.aldiprahasta.tmdb.navigation.Explore
import com.aldiprahasta.tmdb.navigation.Favorite
import com.aldiprahasta.tmdb.navigation.Search
import com.aldiprahasta.tmdb.navigation.TMDbNavHostController
import com.aldiprahasta.tmdb.navigation.currentDestination
import com.aldiprahasta.tmdb.navigation.navigateSingleTopTo
import com.aldiprahasta.tmdb.ui.dashboard.DashboardBottomNavigation
import com.aldiprahasta.tmdb.ui.dashboard.DashboardTab

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentRoute = navController.currentDestination()?.route
    val currentTab = when (currentRoute) {
        Dashboard.route -> DashboardTab.HOME
        Explore.route -> DashboardTab.EXPLORE
        Search.route -> DashboardTab.SEARCH
        Favorite.route -> DashboardTab.FAVORITES
        else -> null
    }
    var lastSelectedTab by rememberSaveable { mutableStateOf(DashboardTab.HOME) }
    SideEffect {
        if (currentTab != null) lastSelectedTab = currentTab
    }
    val showBottomBar = currentTab != null &&
            (currentTab != DashboardTab.SEARCH || WindowInsets.ime.getBottom(LocalDensity.current) == 0)

    Scaffold(
            modifier = modifier,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                AnimatedVisibility(
                        visible = showBottomBar,
                        enter = expandVertically(tween(220, easing = FastOutSlowInEasing)) + fadeIn(tween(180)),
                        exit = shrinkVertically(tween(200, easing = FastOutSlowInEasing)) + fadeOut(tween(180))
                ) {
                    DashboardBottomNavigation(
                            selectedTab = currentTab ?: lastSelectedTab,
                            onTabSelected = { tab ->
                                if (showBottomBar && tab != currentTab) {
                                    val route = when (tab) {
                                        DashboardTab.HOME -> Dashboard.route
                                        DashboardTab.EXPLORE -> Explore.route
                                        DashboardTab.SEARCH -> Search.route
                                        DashboardTab.FAVORITES -> Favorite.route
                                    }
                                    navController.navigateSingleTopTo(route)
                                }
                            }
                    )
                }
            }
    ) { innerPadding ->
        TMDbNavHostController(
                navController = navController,
                modifier = Modifier.padding(innerPadding).clipToBounds()
        )
    }
}

package com.aldiprahasta.tmdb.ui.home

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.aldiprahasta.tmdb.BuildConfig
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.navigation.Dashboard
import com.aldiprahasta.tmdb.navigation.Explore
import com.aldiprahasta.tmdb.navigation.Favorite
import com.aldiprahasta.tmdb.navigation.Search
import com.aldiprahasta.tmdb.navigation.TMDbNavHostController
import com.aldiprahasta.tmdb.navigation.currentDestination
import com.aldiprahasta.tmdb.navigation.navigateSingleTopTo
import com.aldiprahasta.tmdb.navigation.navigationItem
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
    val showTopBar = currentTab == null && navigationItem.any { it.route == currentRoute }
    val showBottomBar = currentTab != null &&
            (currentTab != DashboardTab.SEARCH || WindowInsets.ime.getBottom(LocalDensity.current) == 0)

    NavigationStatusBar(useDarkIcons = !showTopBar)

    Scaffold(
            modifier = modifier,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                AnimatedVisibility(
                        visible = showTopBar,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                ) {
                    TMDbMainTopBar()
                }
            },
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

@Composable
private fun NavigationStatusBar(useDarkIcons: Boolean) {
    val view = LocalView.current
    val window = (view.context as? Activity)?.window ?: return
    SideEffect {
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = useDarkIcons
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TMDbMainTopBar(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    TopAppBar(
            modifier = modifier,
            colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
            ),
            title = {
                Image(
                        painter = painterResource(id = R.drawable.tmdb_logo_long),
                        contentDescription = null,
                        modifier = Modifier
                                .width(120.dp)
                                .clickable {
                                    Toast.makeText(
                                            context,
                                            "TMDb App v${BuildConfig.VERSION_NAME}",
                                            Toast.LENGTH_SHORT
                                    ).show()
                                }
                )
            }
    )
}

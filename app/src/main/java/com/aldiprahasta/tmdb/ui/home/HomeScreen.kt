package com.aldiprahasta.tmdb.ui.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aldiprahasta.tmdb.BuildConfig
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.navigation.TMDbNavHostController
import com.aldiprahasta.tmdb.navigation.currentDestination
import com.aldiprahasta.tmdb.navigation.navigateSingleTopTo
import com.aldiprahasta.tmdb.navigation.navigationItem
import com.aldiprahasta.tmdb.ui.theme.TMDBSecondaryColor

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var isVisibleBar by remember { mutableStateOf(true) }

    isVisibleBar = navigationItem.find {
        it.route == navController.currentDestination()?.route
    } != null

    Scaffold(
            modifier = modifier,
            topBar = {
                AnimatedVisibility(
                        visible = isVisibleBar,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                ) {
                    TMDbMainTopBar()
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

@Composable
private fun TMDbBottomNavigation(
        navController: NavHostController,
        modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        navigationItem.forEach { tmDbDestinations ->
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
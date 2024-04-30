package com.aldiprahasta.tmdb.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import com.aldiprahasta.tmdb.ui.movie.MovieScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
        onMovieClicked: (movieId: Int) -> Unit,
        modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
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
    ) { innerPadding ->
        MovieScreen(
                onMovieClicked = onMovieClicked,
                modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun TMDbBottomNavigation(modifier: Modifier = Modifier) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
                selected = true,
                onClick = {

                },
                icon = {
                    Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null
                    )
                },
                label = {
                    Text(text = "Movie")
                }
        )

        NavigationBarItem(
                selected = false,
                onClick = {

                },
                icon = {
                    Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null
                    )
                },
                label = {
                    Text(text = "Tv Show")
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TMDbBottomNavigationPreview() {
    TMDbBottomNavigation()
}
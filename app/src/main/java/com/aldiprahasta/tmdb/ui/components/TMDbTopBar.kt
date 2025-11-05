package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberTopAppBarScrollBehavior(): TopAppBarScrollBehavior {
    return TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TMDbTopBar(
        scrollBehavior: TopAppBarScrollBehavior,
        topBarTitle: @Composable () -> Unit,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier,
        actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
            modifier = modifier,
            colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
            ),
            scrollBehavior = scrollBehavior,
            title = topBarTitle,
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                    )
                }
            },
            actions = actions
    )
}

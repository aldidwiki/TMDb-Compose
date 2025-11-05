package com.aldiprahasta.tmdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aldiprahasta.tmdb.ui.home.HomeScreen
import com.aldiprahasta.tmdb.ui.theme.TMDbTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(
                        Color.Transparent.toArgb(),
                ),
                navigationBarStyle = SystemBarStyle.light(
                        Color.White.toArgb(),
                        Color.White.toArgb()
                )
        )

        setContent {
            TMDbContent()
        }
    }
}

@Composable
fun TMDbContent() {
    TMDbTheme {
        Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen()
        }
    }
}
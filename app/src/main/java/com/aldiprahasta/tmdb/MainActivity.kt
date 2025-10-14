package com.aldiprahasta.tmdb

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aldiprahasta.tmdb.ui.home.HomeScreen
import com.aldiprahasta.tmdb.ui.theme.TMDbTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            enableEdgeToEdge()
        }
        super.onCreate(savedInstanceState)
        installSplashScreen()

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
package com.aldiprahasta.tmdb.ui.details

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.MovieDetailDomainModel
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import com.aldiprahasta.tmdb.utils.getImageBitmap
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDetailScreen(
        contentId: Int,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: ContentDetailViewModel = koinViewModel()
    viewModel.setId(contentId)
    val movieDetail by viewModel.movieDetail.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var posterPath by remember { mutableStateOf<String?>(null) }
    var palette by remember { mutableStateOf<Palette?>(null) }
    posterPath?.let {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        LaunchedEffect(scope) {
            val bitmap = context.getImageBitmap(it)
            palette = Palette.Builder(bitmap).generate()
        }
    }

    val rgbColor = palette?.vibrantSwatch?.rgb ?: palette?.dominantSwatch?.rgb
    ?: 0
    val titleTextColor = palette?.vibrantSwatch?.titleTextColor
            ?: palette?.dominantSwatch?.titleTextColor
            ?: MaterialTheme.colorScheme.onSurface.toArgb()
    val bodyTextColor = palette?.vibrantSwatch?.bodyTextColor
            ?: palette?.dominantSwatch?.bodyTextColor
            ?: MaterialTheme.colorScheme.onSurface.toArgb()

    SetStatusBarColor(rgbColorPalette = rgbColor)
    Scaffold(
            modifier = modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(rgbColor),
                                titleContentColor = Color(titleTextColor),
                        ),
                        scrollBehavior = scrollBehavior,
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = {
                                onBackPressed()
                            }) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back Button",
                                        tint = Color(titleTextColor)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { }) {
                                Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share Button",
                                        tint = Color(titleTextColor)
                                )
                            }
                        }
                )
            }) { innerPadding ->
        ContentDetail(
                movieDetail = movieDetail,
                modifier = Modifier.padding(innerPadding),
                colorPalette = Triple(Color(rgbColor), Color(titleTextColor), Color(bodyTextColor)),
                onSuccessFetch = {
                    posterPath = it.posterPath
                }
        )
    }
}

@Composable
private fun SetStatusBarColor(rgbColorPalette: Int) {
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val isDarkMode = isSystemInDarkTheme()
    val context = LocalContext.current as ComponentActivity

    DisposableEffect(isDarkMode) {
        context.enableEdgeToEdge(
                statusBarStyle = if (!isDarkMode) {
                    SystemBarStyle.light(
                            rgbColorPalette,
                            primaryColor
                    )
                } else {
                    SystemBarStyle.dark(
                            primaryColor
                    )
                },
                navigationBarStyle = if (!isDarkMode) {
                    SystemBarStyle.light(
                            rgbColorPalette,
                            primaryColor
                    )
                } else {
                    SystemBarStyle.dark(
                            primaryColor
                    )
                }
        )

        onDispose {
            context.enableEdgeToEdge(
                    statusBarStyle = if (!isDarkMode) {
                        SystemBarStyle.light(
                                primaryColor,
                                primaryColor
                        )
                    } else {
                        SystemBarStyle.dark(
                                primaryColor
                        )
                    },
                    navigationBarStyle = if (!isDarkMode) {
                        SystemBarStyle.light(
                                rgbColorPalette,
                                primaryColor
                        )
                    } else {
                        SystemBarStyle.dark(
                                primaryColor
                        )
                    }
            )
        }
    }
}

@Composable
private fun ContentDetail(
        movieDetail: UiState<MovieDetailDomainModel>,
        colorPalette: Triple<Color, Color, Color>,
        onSuccessFetch: (movieDetail: MovieDetailDomainModel) -> Unit,
        modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        movieDetail.doIfLoading {
            LoadingScreen()
        }

        movieDetail.doIfError { throwable, _ ->
            ErrorScreen(errorMessage = throwable.localizedMessage ?: "")
        }

        movieDetail.doIfSuccess { movieDetailDomainModel ->
            Column(modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()))
            {
                ContentDetailCard(
                        movieDetailDomainModel = movieDetailDomainModel,
                        colorPalette = colorPalette
                )
                Spacer(modifier = Modifier.size(20.dp))
                ContentBilledCast(
                        casts = movieDetailDomainModel.casts,
                )
                Spacer(modifier = Modifier.size(20.dp))
                ContentDetailInfo(
                        status = movieDetailDomainModel.status,
                        originalLanguage = movieDetailDomainModel.originalLanguage,
                        budget = movieDetailDomainModel.budget,
                        revenue = movieDetailDomainModel.revenue,
                        modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 20.dp)
                )
            }

            onSuccessFetch(movieDetailDomainModel)
        }
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
fun ContentDetailPreview() {
    ContentDetail(
            movieDetail = UiState.Success(data = MovieDetailDomainModel(title = "Dune: Part Two",
                    posterPath = null,
                    releaseDate = "27 February 2024",
                    runtime = "2h 27m",
                    tagline = "Long live the fighters.",
                    overview = "Follow the mythic journey of Paul Atreides as he unites with Chani and the Fremen while on a path of revenge against the conspirators who destroyed his family. Facing a choice between the love of his life and the fate of the known universe, Paul endeavors to prevent a terrible future only he can foresee.",
                    id = 693134,
                    voteAverage = 8.291,
                    genres = "Adventures, Science Fiction",
                    certification = "PG-13",
                    backdropPath = null,
                    casts = listOf(
                            CastDomainModel(
                                    name = "Timothée Chalamet",
                                    characterName = "Paul Atreides",
                                    profilePath = null,
                                    order = 0
                            ),
                            CastDomainModel(
                                    name = "Timothée Chalamet",
                                    characterName = "Paul Atreides",
                                    profilePath = null,
                                    order = 0
                            ),
                            CastDomainModel(
                                    name = "Timothée Chalamet",
                                    characterName = "Paul Atreides",
                                    profilePath = null,
                                    order = 0
                            ),
                            CastDomainModel(
                                    name = "Timothée Chalamet",
                                    characterName = "Paul Atreides",
                                    profilePath = null,
                                    order = 0
                            )
                    ),
                    budget = "$1,000,000.00",
                    revenue = "$2,000,000.00",
                    originalLanguage = "English",
                    status = "Released"
            )),
            colorPalette = Triple(Color.White, Color.Black, Color.Black),
            onSuccessFetch = {}
    )
}
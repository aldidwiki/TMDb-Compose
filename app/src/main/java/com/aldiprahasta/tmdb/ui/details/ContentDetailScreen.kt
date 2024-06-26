package com.aldiprahasta.tmdb.ui.details

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.ExternalIdDomainModel
import com.aldiprahasta.tmdb.ui.components.ContentBilledCast
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import com.aldiprahasta.tmdb.utils.getImageBitmap
import com.aldiprahasta.tmdb.utils.shareIt
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDetailScreen(
        contentParam: Pair<Int, String>,
        onBackPressed: () -> Unit,
        onCastClicked: (personId: Int) -> Unit,
        onViewMoreClicked: () -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: ContentDetailViewModel = koinViewModel()
    viewModel.setId(contentParam)
    val contentDetail by viewModel.contentDetail.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val context = LocalContext.current
    var posterPath by remember { mutableStateOf<String?>(null) }
    var palette by remember { mutableStateOf<Palette?>(null) }
    posterPath?.let {
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
                            IconButton(onClick = {
                                val shareUrl = if (contentParam.second == MediaType.MOVIE_TYPE.name) {
                                    "${Constant.SHARE_BASE_URL}/movie/${contentParam.first}"
                                } else {
                                    "${Constant.SHARE_BASE_URL}/tv/${contentParam.first}"
                                }

                                context.shareIt(shareUrl)
                            }) {
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
                contentDetail = contentDetail,
                modifier = modifier.padding(innerPadding),
                colorPalette = Triple(Color(rgbColor), Color(titleTextColor), Color(bodyTextColor)),
                onSuccessFetch = {
                    posterPath = it.posterPath
                },
                onCastClicked = { personId ->
                    onCastClicked(personId)
                },
                onViewMoreClicked = onViewMoreClicked
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
        contentDetail: UiState<ContentDetailDomainModel>,
        colorPalette: Triple<Color, Color, Color>,
        onSuccessFetch: (contentDetail: ContentDetailDomainModel) -> Unit,
        onCastClicked: (personId: Int) -> Unit,
        onViewMoreClicked: () -> Unit,
        modifier: Modifier = Modifier
) {
    AnimatedContent(
            targetState = contentDetail,
            label = "Animated Content",
            modifier = modifier.fillMaxSize()
    ) { targetState ->
        targetState.doIfLoading {
            LoadingScreen()
        }

        targetState.doIfError { throwable, _ ->
            ErrorScreen(errorMessage = throwable.localizedMessage ?: "")
        }

        targetState.doIfSuccess { contentDetailDomainModel ->
            Column(modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()))
            {
                ContentDetailCard(
                        contentDetailDomainModel = contentDetailDomainModel,
                        colorPalette = colorPalette
                )
                Spacer(modifier = Modifier.size(20.dp))
                ContentBilledCast(
                        sectionTitle = "Top Billed Cast",
                        casts = contentDetailDomainModel.casts,
                        onCastClicked = { personId, _ ->
                            onCastClicked(personId)
                        },
                        onViewMoreClicked = onViewMoreClicked
                )
                Spacer(modifier = Modifier.size(20.dp))
                ContentDetailInfo(
                        status = contentDetailDomainModel.status,
                        originalLanguage = contentDetailDomainModel.originalLanguage,
                        budget = contentDetailDomainModel.budget,
                        revenue = contentDetailDomainModel.revenue,
                        networks = contentDetailDomainModel.networks,
                        tvType = contentDetailDomainModel.type,
                        modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.size(20.dp))
                ContentDetailExternal(
                        instagramId = contentDetailDomainModel.externalId.instragramId,
                        facebookId = contentDetailDomainModel.externalId.facebookId,
                        twitterId = contentDetailDomainModel.externalId.twitterId,
                        imdbPair = Pair(true, contentDetailDomainModel.externalId.imdbId),
                        googleId = contentDetailDomainModel.title,
                        modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 20.dp)
                )
            }

            onSuccessFetch(contentDetailDomainModel)
        }
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
fun ContentDetailPreview() {
    ContentDetail(
            contentDetail = UiState.Success(data = ContentDetailDomainModel(
                    title = "Dune: Part Two",
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
                                    imagePath = null,
                                    order = 0,
                                    id = 12345,
                                    mediaType = null,
                                    releaseDate = "",
                                    totalEpisodeCount = 10
                            ),
                            CastDomainModel(
                                    name = "Timothée Chalamet",
                                    characterName = "Paul Atreides",
                                    imagePath = null,
                                    order = 0,
                                    id = 12345,
                                    mediaType = null,
                                    releaseDate = "",
                                    totalEpisodeCount = 10
                            ),
                            CastDomainModel(
                                    name = "Timothée Chalamet",
                                    characterName = "Paul Atreides",
                                    imagePath = null,
                                    order = 0,
                                    id = 12345,
                                    mediaType = null,
                                    releaseDate = "",
                                    totalEpisodeCount = 10
                            ),
                            CastDomainModel(
                                    name = "Timothée Chalamet",
                                    characterName = "Paul Atreides",
                                    imagePath = null,
                                    order = 0,
                                    id = 12345,
                                    mediaType = null,
                                    releaseDate = "",
                                    totalEpisodeCount = 10
                            )
                    ),
                    budget = "$1,000,000.00",
                    revenue = "$2,000,000.00",
                    originalLanguage = "English",
                    status = "Released",
                    externalId = ExternalIdDomainModel(
                            instragramId = "",
                            facebookId = "",
                            imdbId = "",
                            twitterId = ""
                    ),
                    videos = emptyList(),
                    type = null,
                    networks = null
            )),
            colorPalette = Triple(Color.White, Color.Black, Color.Black),
            onSuccessFetch = {},
            onCastClicked = {},
            onViewMoreClicked = {}
    )
}
package com.aldiprahasta.tmdb.ui.tv.tvseason

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.domain.model.TvEpisodeDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.TMDbTopBar
import com.aldiprahasta.tmdb.ui.components.rememberTopAppBarScrollBehavior
import com.aldiprahasta.tmdb.ui.tv.TvEpisodeItem
import com.aldiprahasta.tmdb.ui.tv.TvViewModel
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvSeasonDetailScreen(
        tvId: Int,
        tvSeasonNumber: Int,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: TvViewModel = koinViewModel()
    viewModel.setTvSeasonDetailParam(tvId, tvSeasonNumber)

    val tvSeasonDetail by viewModel.tvSeasonDetail.collectAsStateWithLifecycle()

    val scrollBehavior = rememberTopAppBarScrollBehavior()

    var tvSeasonName by remember { mutableStateOf("") }
    var tvSeasonYear by remember { mutableStateOf("") }

    Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TMDbTopBar(
                        topBarTitle = {
                            val titleText = if (tvSeasonYear.isEmpty() || tvSeasonName.isEmpty()) {
                                "Episodes List"
                            } else {
                                "$tvSeasonName ($tvSeasonYear)"
                            }
                            Text(text = titleText)
                        },
                        scrollBehavior = scrollBehavior,
                        onBackPressed = onBackPressed
                )
            }
    ) { innerPadding ->
        AnimatedContent(
                targetState = tvSeasonDetail,
                label = "Animated Content",
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(tween(500))
                },
                modifier = Modifier.padding(innerPadding)
        ) { targetState ->
            targetState.doIfLoading {
                LoadingScreen()
            }

            targetState.doIfError { throwable, _ ->
                ErrorScreen()
            }

            targetState.doIfSuccess { data: TvSeasonDetailDomainModel ->
                tvSeasonName = data.tvSeasonDomainModel.seasonName
                tvSeasonYear = data.tvSeasonDomainModel.seasonAirDate.takeLast(4)
                TvSeasonDetailContent(tvEpisodeList = data.tvEpisodeList)
            }
        }
    }
}

@Composable
private fun TvSeasonDetailContent(
        tvEpisodeList: List<TvEpisodeDomainModel>,
        modifier: Modifier = Modifier
) {
    LazyColumn(
            contentPadding = PaddingValues(
                    vertical = 10.dp,
                    horizontal = 16.dp
            ),
            modifier = modifier
    ) {
        items(tvEpisodeList) { tvEpisode ->
            TvEpisodeItem(
                    tvEpisodeDomainModel = tvEpisode,
                    modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TvSeasonDetailContentPreview() {
    TvSeasonDetailContent(
            tvEpisodeList = listOf(
                    TvEpisodeDomainModel(
                            id = 4755,
                            name = "The Heirs of the Dragon",
                            stillPath = null,
                            runtime = 66,
                            overview = "Viserys hosts a tournament to celebrate the birth of his second child. Rhaenyra welcomes her uncle Daemon back to the Red Keep.",
                            voteAverage = 7.9,
                            episodeNumber = 1,
                            airDate = "August 21, 2022"
                    ),
                    TvEpisodeDomainModel(
                            id = 4755,
                            name = "The Heirs of the Dragon",
                            stillPath = null,
                            runtime = 66,
                            overview = "Viserys hosts a tournament to celebrate the birth of his second child. Rhaenyra welcomes her uncle Daemon back to the Red Keep.",
                            voteAverage = 7.9,
                            episodeNumber = 1,
                            airDate = "August 21, 2022"
                    ),
                    TvEpisodeDomainModel(
                            id = 4755,
                            name = "The Heirs of the Dragon",
                            stillPath = null,
                            runtime = 66,
                            overview = "Viserys hosts a tournament to celebrate the birth of his second child. Rhaenyra welcomes her uncle Daemon back to the Red Keep.",
                            voteAverage = 7.9,
                            episodeNumber = 1,
                            airDate = "August 21, 2022"
                    ),
                    TvEpisodeDomainModel(
                            id = 4755,
                            name = "The Heirs of the Dragon",
                            stillPath = null,
                            runtime = 66,
                            overview = "Viserys hosts a tournament to celebrate the birth of his second child. Rhaenyra welcomes her uncle Daemon back to the Red Keep.",
                            voteAverage = 7.9,
                            episodeNumber = 1,
                            airDate = "August 21, 2022"
                    )
            )
    )
}
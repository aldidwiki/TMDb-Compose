package com.aldiprahasta.tmdb.ui.tv

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvSeasonScreen(
        tvTitle: String,
        tvSeasonList: List<TvSeasonDomainModel>,
        onItemClicked: (tvSeasonNumber: Int) -> Unit,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
            topBar = {
                TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = Color.White,
                        ),
                        scrollBehavior = scrollBehavior,
                        title = {
                            Text(
                                    text = "$tvTitle's Seasons",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackPressed) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        tint = Color.White,
                                        contentDescription = null
                                )
                            }
                        }
                )
            },
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp)
            ) {
                itemsIndexed(tvSeasonList) { index, tvSeason ->
                    TvSeasonItem(
                            tvTitle = tvTitle,
                            tvSeasonDomainModel = tvSeason,
                            onItemClicked = {
                                onItemClicked(tvSeason.seasonNumber)
                            }
                    )

                    if (index < tvSeasonList.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TvSeasonScreenPreview(modifier: Modifier = Modifier) {
    TvSeasonScreen(
            tvTitle = "House of Dragon",
            tvSeasonList = listOf(
                    TvSeasonDomainModel(
                            seasonId = 2237,
                            seasonName = "Season 1",
                            seasonPosterPath = null,
                            seasonAirDate = "August 10, 2022",
                            seasonVoteAverage = 8.0,
                            totalEpisodes = 53,
                            seasonNumber = 1
                    ),
                    TvSeasonDomainModel(
                            seasonId = 2237,
                            seasonName = "Season 1",
                            seasonPosterPath = null,
                            seasonAirDate = "August 10, 2022",
                            seasonVoteAverage = 8.0,
                            totalEpisodes = 53,
                            seasonNumber = 1
                    ),
                    TvSeasonDomainModel(
                            seasonId = 2237,
                            seasonName = "Season 1",
                            seasonPosterPath = null,
                            seasonAirDate = "August 10, 2022",
                            seasonVoteAverage = 8.0,
                            totalEpisodes = 53,
                            seasonNumber = 1
                    ),
                    TvSeasonDomainModel(
                            seasonId = 2237,
                            seasonName = "Season 1",
                            seasonPosterPath = null,
                            seasonAirDate = "August 10, 2022",
                            seasonVoteAverage = 8.0,
                            totalEpisodes = 53,
                            seasonNumber = 1
                    ),
            ),
            onBackPressed = {},
            onItemClicked = {}
    )
}
package com.aldiprahasta.tmdb.ui.credit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.ui.components.ContentItem
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.theme.TMDBSecondaryColor
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import org.koin.androidx.compose.koinViewModel

private val characterComparator = Comparator<CastDomainModel> { left, right ->
    left.characterName.compareTo(right.characterName)
}

private val nameComparator = Comparator<CastDomainModel> { left, right ->
    left.name.compareTo(right.name)
}

private val orderComparator = Comparator<CastDomainModel> { left, right ->
    left.order.compareTo(right.order)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditScreen(
        contentId: Pair<Int, String>,
        onBackPressed: () -> Unit,
        onItemClicked: (contentId: Int, mediaType: String?) -> Unit,
        modifier: Modifier = Modifier
) {
    val creditViewModel: CreditViewModel = koinViewModel()
    creditViewModel.setContentId(contentId)
    val creditData by creditViewModel.credits.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                        title = {
                            Text(text = "Full Casts")
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = Color.White,
                        ),
                        navigationIcon = {
                            IconButton(onClick = onBackPressed) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        tint = Color.White,
                                        contentDescription = null
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                )
            }) { innerPadding ->
        AnimatedContent(
                targetState = creditData,
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
                ErrorScreen(errorMessage = throwable.localizedMessage ?: "")
            }

            targetState.doIfSuccess { casts ->
                CreditContent(
                        casts = casts,
                        onItemClicked = { contentId, mediaType ->
                            onItemClicked(contentId, mediaType)
                        }
                )
            }
        }
    }
}

@Composable
fun CreditContent(
        casts: List<CastDomainModel>,
        onItemClicked: (contentId: Int, mediaType: String?) -> Unit,
        modifier: Modifier = Modifier
) {
    LazyColumn(
            contentPadding = PaddingValues(10.dp),
            modifier = modifier
    ) {
        itemsIndexed(casts) { index, item ->
            ContentItem(
                    title = item.name,
                    releaseDate = item.releaseDate,
                    posterPath = item.imagePath,
                    characterName = item.characterName,
                    onItemClicked = {
                        onItemClicked(item.id, item.mediaType)
                    },
                    totalEpisodeCount = item.totalEpisodeCount,
            )

            if (index < casts.lastIndex) {
                HorizontalDivider(Modifier.padding(vertical = 16.dp))
            }
        }
    }
}

@Composable
private fun SortingChip(
        onSelectedChip: (comparator: Comparator<CastDomainModel>) -> Unit,
        modifier: Modifier = Modifier
) {
    val chips = mapOf(
            "character name" to characterComparator,
            "name" to nameComparator
    )
    var selectedChip by remember { mutableStateOf("") }

    Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier.fillMaxWidth()
    ) {
        chips.forEach { (label, comparator) ->
            FilterChip(
                    colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TMDBSecondaryColor,
                            selectedLabelColor = Color.White
                    ),
                    selected = label == selectedChip,
                    onClick = {
                        onSelectedChip(comparator)
                        selectedChip = if (label != selectedChip) label
                        else {
                            onSelectedChip(orderComparator) // default comparator
                            ""
                        }
                    },
                    label = {
                        Text(text = label)
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreditContentPreview() {
    CreditContent(
            casts = listOf(
                    CastDomainModel(
                            id = 3728,
                            name = "Aldo Nolan",
                            characterName = "Eugenia Nolan",
                            imagePath = null,
                            order = 2653,
                            mediaType = null,
                            releaseDate = "",
                            totalEpisodeCount = 10
                    ),
                    CastDomainModel(
                            id = 3728,
                            name = "Aldo Nolan",
                            characterName = "Eugenia Nolan",
                            imagePath = null,
                            order = 2653,
                            mediaType = null,
                            releaseDate = "",
                            totalEpisodeCount = 10
                    ),
                    CastDomainModel(
                            id = 3728,
                            name = "Aldo Nolan",
                            characterName = "Eugenia Nolan",
                            imagePath = null,
                            order = 2653,
                            mediaType = null,
                            releaseDate = "",
                            totalEpisodeCount = 10
                    ),
                    CastDomainModel(
                            id = 3728,
                            name = "Aldo Nolan",
                            characterName = "Eugenia Nolan",
                            imagePath = null,
                            order = 2653,
                            mediaType = null,
                            releaseDate = "",
                            totalEpisodeCount = 10
                    ),
                    CastDomainModel(
                            id = 3728,
                            name = "Aldo Nolan",
                            characterName = "Eugenia Nolan",
                            imagePath = null,
                            order = 2653,
                            mediaType = null,
                            releaseDate = "",
                            totalEpisodeCount = 10
                    )
            ),
            onItemClicked = { _, _ -> }
    )
}

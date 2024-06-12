package com.aldiprahasta.tmdb.ui.credit

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.runtime.getValue
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
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditScreen(
        contentId: Pair<Int, String>,
        onBackPressed: () -> Unit,
        onItemClicked: (contentId: Int) -> Unit,
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
                        onItemClicked = onItemClicked
                )
            }
        }
    }
}

@Composable
fun CreditContent(
        casts: List<CastDomainModel>,
        onItemClicked: (contentId: Int) -> Unit,
        modifier: Modifier = Modifier
) {
    LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(10.dp)
    ) {
        itemsIndexed(casts) { index, item ->
            ContentItem(
                    title = item.name,
                    releaseDate = item.releaseDate,
                    posterPath = item.imagePath,
                    characterName = item.characterName,
                    onItemClicked = {
                        onItemClicked(item.id)
                    }
            )

            if (index < casts.lastIndex) {
                HorizontalDivider(Modifier.padding(vertical = 16.dp))
            }
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
            onItemClicked = {}
    )
}

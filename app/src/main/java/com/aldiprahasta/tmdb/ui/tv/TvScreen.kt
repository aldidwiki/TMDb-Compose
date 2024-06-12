package com.aldiprahasta.tmdb.ui.tv

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.domain.model.TvDomainModel
import com.aldiprahasta.tmdb.ui.components.ContentItem
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import org.koin.androidx.compose.koinViewModel

@Composable
fun TvScreen(
        onItemClicked: (tvId: Int) -> Unit,
        modifier: Modifier = Modifier
) {
    val tvViewModel: TvViewModel = koinViewModel()
    val tvData by tvViewModel.popularTv.collectAsStateWithLifecycle()

    AnimatedContent(
            targetState = tvData,
            label = "Animated Content",
            modifier = modifier.fillMaxWidth()
    ) { targetState ->
        targetState.doIfLoading {
            LoadingScreen()
        }

        targetState.doIfError { throwable, _ ->
            ErrorScreen(errorMessage = throwable.localizedMessage ?: "")
        }

        targetState.doIfSuccess { popularTvList ->
            TvContent(
                    popularTvList = popularTvList,
                    onItemClicked = onItemClicked
            )
        }
    }
}

@Composable
fun TvContent(
        popularTvList: List<TvDomainModel>,
        onItemClicked: (tvId: Int) -> Unit,
        modifier: Modifier = Modifier
) {
    LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(10.dp)
    ) {
        itemsIndexed(popularTvList) { index, item ->
            ContentItem(
                    title = item.title,
                    releaseDate = item.releaseDate,
                    characterName = null,
                    posterPath = item.posterPath,
                    onItemClicked = {
                        onItemClicked(item.tvId)
                    }
            )

            if (index < popularTvList.lastIndex) {
                HorizontalDivider(Modifier.padding(vertical = 16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TvContentPreview() {
    TvContent(
            popularTvList = listOf(
                    TvDomainModel(
                            posterPath = null,
                            tvId = 9989,
                            title = "omnesque",
                            releaseDate = "dicam"
                    ),
                    TvDomainModel(
                            posterPath = null,
                            tvId = 9989,
                            title = "omnesque",
                            releaseDate = "dicam"
                    ),
                    TvDomainModel(
                            posterPath = null,
                            tvId = 9989,
                            title = "omnesque",
                            releaseDate = "dicam"
                    ),
                    TvDomainModel(
                            posterPath = null,
                            tvId = 9989,
                            title = "omnesque",
                            releaseDate = "dicam"
                    )
            ),
            onItemClicked = {}
    )
}

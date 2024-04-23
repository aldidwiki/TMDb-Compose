package com.aldiprahasta.tmdb.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aldiprahasta.tmdb.domain.model.MovieDetailDomainModel
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.ImageLoader
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import com.aldiprahasta.tmdb.utils.formatVoteAverage

@Composable
fun ContentDetailScreen(contentId: Int, modifier: Modifier = Modifier) {
    val viewModel: ContentDetailViewModel = viewModel()
    viewModel.setId(contentId)
    val movieDetail by viewModel.movieDetail.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        ContentDetailCard(movieDetail = movieDetail)
    }
}

@Composable
private fun ContentDetailCard(movieDetail: UiState<MovieDetailDomainModel>, modifier: Modifier = Modifier) {
    Column(modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        movieDetail.doIfLoading {
            LoadingScreen()
        }

        movieDetail.doIfError { throwable, _ ->
            ErrorScreen(errorMessage = throwable.localizedMessage ?: "")
        }

        movieDetail.doIfSuccess { movieDetailDomainModel ->
            ContentDetailPosterWithInfo(
                    posterPath = movieDetailDomainModel.posterPath,
                    title = movieDetailDomainModel.title,
                    releaseDate = movieDetailDomainModel.releaseDate,
                    runtime = movieDetailDomainModel.runtime,
                    tagline = movieDetailDomainModel.tagline
            )
            Spacer(modifier = Modifier.size(10.dp))
            ContentDetailUserScoreWithTrailer(voteAverage = movieDetailDomainModel.voteAverage)
            Spacer(modifier = Modifier.size(10.dp))
            ContentOverview(overview = movieDetailDomainModel.overview)
        }
    }
}

@Composable
private fun ContentOverview(
        overview: String,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
                text = overview,
                style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ContentDetailUserScoreWithTrailer(
        voteAverage: Double,
        modifier: Modifier = Modifier
) {
    Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 3.dp,
                    progress = {
                        voteAverage.formatVoteAverage() / 100f
                    }
            )
            Text(text = "${voteAverage.formatVoteAverage()}%", style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "User Score", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.size(20.dp))
        Text(text = "\u007C", style = MaterialTheme.typography.labelMedium)
        TextButton(onClick = { }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = "Play Trailer")
            }
        }
    }
}

@Composable
private fun ContentDetailPosterWithInfo(
        posterPath: String?,
        title: String,
        releaseDate: String,
        runtime: String,
        tagline: String,
        modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        ImageLoader(
                imagePath = posterPath,
                imageType = ImageType.POSTER
        )
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp)
        ) {
            Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
            )
            Text(
                    text = releaseDate,
                    style = MaterialTheme.typography.bodySmall
            )
            Row {
                Text(
                        text = runtime,
                        style = MaterialTheme.typography.bodySmall
                )
                Text(
                        text = "\u2022",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                        text = "PG-13",
                        style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                    text = "Adventure, Science Fiction",
                    style = MaterialTheme.typography.bodySmall
            )
            Text(
                    text = tagline,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
fun ContentDetailCardPreview() {
    ContentDetailCard(UiState.Success(data = MovieDetailDomainModel(
            title = "Dune: Part Two",
            posterPath = null,
            releaseDate = "27 February 2024",
            runtime = "2h 27m",
            tagline = "Long live the fighters.",
            overview = "Follow the mythic journey of Paul Atreides as he unites with Chani and the Fremen while on a path of revenge against the conspirators who destroyed his family. Facing a choice between the love of his life and the fate of the known universe, Paul endeavors to prevent a terrible future only he can foresee.",
            id = 693134,
            voteAverage = 8.291
    )))
}
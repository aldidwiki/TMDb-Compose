package com.aldiprahasta.tmdb.ui.details

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.ExternalIdDomainModel
import com.aldiprahasta.tmdb.domain.model.VideoDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageLoader
import com.aldiprahasta.tmdb.ui.components.ImageLoaderBackdrop
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.PaletteColors
import com.aldiprahasta.tmdb.utils.formatVoteAverage
import com.aldiprahasta.tmdb.utils.isColorLight
import com.aldiprahasta.tmdb.utils.openBrowser
import com.aldiprahasta.tmdb.utils.windowHeightFraction

@Composable
fun ContentDetailCard(
        contentDetailDomainModel: ContentDetailDomainModel,
        colorPalette: PaletteColors,
        showBottomSheet: Boolean,
        onShowBottomSheetChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
) {
    Surface(
            color = Color(colorPalette.rgbColor),
            shadowElevation = 6.dp,
            modifier = modifier.fillMaxWidth()
    ) {
        Column {
            contentDetailDomainModel.backdropPath?.let { path ->
                ImageLoaderBackdrop(imagePath = path)
            }
            ContentDetailPosterWithInfo(
                    posterPath = contentDetailDomainModel.posterPath,
                    title = contentDetailDomainModel.title,
                    releaseDate = contentDetailDomainModel.releaseDate,
                    runtime = contentDetailDomainModel.runtime,
                    tagline = contentDetailDomainModel.tagline,
                    genres = contentDetailDomainModel.genres,
                    certification = contentDetailDomainModel.certification,
                    colorPalette = colorPalette,
                    modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 10.dp)
            )
            Spacer(modifier = Modifier.size(10.dp))
            ContentDetailUserScoreWithTrailer(
                    voteAverage = contentDetailDomainModel.voteAverage,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colorPalette = colorPalette,
                    videos = contentDetailDomainModel.videos,
                    showBottomSheet = showBottomSheet,
                    onShowBottomSheetChange = onShowBottomSheetChange
            )
            Spacer(modifier = Modifier.size(10.dp))
            ContentOverview(
                    overview = contentDetailDomainModel.overview,
                    colorPalette = colorPalette,
                    modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun ContentOverview(
        overview: String,
        colorPalette: PaletteColors,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium,
                color = Color(colorPalette.titleTextColor)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
                text = overview,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Justify,
                color = Color(colorPalette.bodyTextColor)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentDetailUserScoreWithTrailer(
        voteAverage: Double,
        colorPalette: PaletteColors,
        videos: List<VideoDomainModel>,
        showBottomSheet: Boolean,
        onShowBottomSheetChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
) {
    Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()
    ) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val context = LocalContext.current
        val sheetHeightDp = windowHeightFraction(0.70f)

        val trailers = videos.asSequence().filter { model ->
            model.type.equals("trailer", ignoreCase = true) &&
                    model.site.equals("youtube", ignoreCase = true)
        }.sortedBy { it.name }.toList()

        if (showBottomSheet) {
            ModalBottomSheet(
                    properties = ModalBottomSheetProperties(
                            isAppearanceLightStatusBars = isColorLight(colorPalette.rgbColor),
                            isAppearanceLightNavigationBars = true,
                    ),
                    onDismissRequest = {
                        onShowBottomSheetChange(false)
                    },
                    sheetState = sheetState
            ) {
                Column(modifier = Modifier.heightIn(max = sheetHeightDp)) {
                    Text(
                            text = "Trailers",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    LazyColumn(modifier = Modifier.padding(10.dp)) {
                        items(trailers) { model ->
                            TextButton(
                                    onClick = {
                                        context.openBrowser(Constant.YOUTUBE_BASE_URL + model.key)
                                    }) {
                                Text(text = model.name)
                            }
                        }
                    }
                }
            }
        }

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 3.dp,
                    trackColor = Color(colorPalette.rgbColor),
                    color = Color(colorPalette.titleTextColor),
                    progress = {
                        voteAverage.formatVoteAverage() / 100f
                    }
            )
            Text(
                    text = "${voteAverage.formatVoteAverage()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(colorPalette.bodyTextColor)
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(
                text = "User Score",
                style = MaterialTheme.typography.labelMedium,
                color = Color(colorPalette.bodyTextColor)
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
                text = "\u007C",
                style = MaterialTheme.typography.labelMedium,
                color = Color(colorPalette.titleTextColor)
        )
        TextButton(onClick = {
            when {
                trailers.isEmpty() -> {
                    Toast.makeText(context, "Sorry, trailer not available", Toast.LENGTH_SHORT).show()
                }

                trailers.size == 1 -> {
                    context.openBrowser(Constant.YOUTUBE_BASE_URL + trailers.first().key)
                }

                else -> {
                    onShowBottomSheetChange(true)
                }
            }
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(colorPalette.titleTextColor)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                        text = "Play Trailer",
                        color = Color(colorPalette.bodyTextColor)
                )
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
        genres: String,
        certification: String,
        colorPalette: PaletteColors,
        modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        ImageLoader(
                imagePath = posterPath,
                imageType = ImageType.POSTER,
                modifier = Modifier
                        .width(120.dp)
        )
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp)
                        .padding(horizontal = 16.dp)
        ) {
            Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(colorPalette.titleTextColor)
            )
            Text(
                    text = releaseDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(colorPalette.bodyTextColor)
            )
            Row {
                if (runtime.isNotEmpty()) {
                    Text(
                            text = runtime,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(colorPalette.bodyTextColor)
                    )
                    Text(
                            text = "\u2022",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 4.dp),
                            color = Color(colorPalette.bodyTextColor)
                    )
                }
                Text(
                        text = certification,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(colorPalette.bodyTextColor)
                )
            }
            Text(
                    text = genres,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(colorPalette.bodyTextColor)
            )
            Text(
                    text = tagline,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp),
                    color = Color(colorPalette.bodyTextColor)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentDetailCardPreview() {
    ContentDetailCard(
            contentDetailDomainModel = ContentDetailDomainModel(
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
                    casts = emptyList(),
                    budget = "190000000",
                    revenue = "696901644",
                    originalLanguage = "English",
                    status = "Released",
                    externalId = ExternalIdDomainModel(
                            instragramId = "",
                            facebookId = "",
                            imdbId = "",
                            twitterId = ""
                    ),
                    videos = emptyList(),
                    type = "",
                    networks = emptyList(),
            ),
            colorPalette = PaletteColors(
                    Color.White.toArgb(),
                    Color.Black.toArgb(),
                    Color.Black.toArgb()
            ),
            showBottomSheet = false,
            onShowBottomSheetChange = {},
    )
}


package com.aldiprahasta.tmdb.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.ui.components.ImageLoader
import com.aldiprahasta.tmdb.ui.components.ImageType

@Composable
fun ContentDetailCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ContentDetailPosterWithInfo()
        Spacer(modifier = Modifier.size(10.dp))
        ContentDetailUserScoreWithTrailer()
        Spacer(modifier = Modifier.size(10.dp))
        ContentOverview()
    }
}

@Composable
private fun ContentOverview(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
                text = "Follow the mythic journey of Paul Atreides as he unites with Chani and the Fremen while on a path of revenge against the conspirators who destroyed his family. Facing a choice between the love of his life and the fate of the known universe, Paul endeavors to prevent a terrible future only he can foresee.",
                style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ContentDetailUserScoreWithTrailer(modifier: Modifier = Modifier) {
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
                        0.5f
                    }
            )
            Text(text = "83%", style = MaterialTheme.typography.labelSmall)
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
private fun ContentDetailPosterWithInfo(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        ImageLoader(
                imagePath = "/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg",
                imageType = ImageType.POSTER
        )
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
        ) {
            Text(
                    text = "Dune: Part Two",
                    style = MaterialTheme.typography.titleMedium
            )
            Text(
                    text = "27 February 2024",
                    style = MaterialTheme.typography.bodySmall
            )
            Row {
                Text(
                        text = "2h 47m",
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
                    text = "Long live the fighters.",
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
    ContentDetailCard()
}
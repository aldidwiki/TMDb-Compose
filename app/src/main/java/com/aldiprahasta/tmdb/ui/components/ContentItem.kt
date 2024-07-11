package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ContentItem(
        title: String,
        releaseDate: String?,
        characterName: String?,
        posterPath: String?,
        totalEpisodeCount: Int?,
        onItemClicked: () -> Unit,
        modifier: Modifier = Modifier
) {
    Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                    .fillMaxWidth()
                    .clickable { onItemClicked() },
    ) {
        ImageLoader(
                imagePath = posterPath,
                imageType = ImageType.POSTER,
                modifier = Modifier
                        .width(100.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
            )
            if (!releaseDate.isNullOrEmpty()) {
                Text(
                        text = releaseDate,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                )
            }
            if (!characterName.isNullOrEmpty()) {
                Text(
                        text = characterName,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                )
            }
            totalEpisodeCount?.let {
                Text(
                        text = "($it episodes)",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 420)
@Composable
fun ContentItemPreview() {
    ContentItem(
            title = "Dune: Part Two",
            releaseDate = "24 December 2024",
            posterPath = null,
            characterName = "Albert",
            onItemClicked = {},
            totalEpisodeCount = 15
    )
}
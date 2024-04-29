package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.domain.model.CastDomainModel

@Composable
fun ContentBilledCast(
        sectionTitle: String,
        casts: List<CastDomainModel>,
        onCastClicked: (contentId: Int, mediaType: String?) -> Unit,
        onViewMoreClicked: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            itemsIndexed(casts.take(10)) { index, item ->
                BilledCastItem(
                        name = item.name,
                        characterName = item.characterName,
                        profilePath = item.imagePath,
                        onItemClicked = {
                            onCastClicked(item.id, item.mediaType)
                        }
                )

                if (index == casts.take(10).lastIndex) {
                    TextButton(onClick = onViewMoreClicked) {
                        Text(text = "View more")
                        Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
fun ContentBilledCastPreview() {
    ContentBilledCast(
            sectionTitle = "Top Billed Cast",
            casts = listOf(
                    CastDomainModel(
                            name = "Timothée Chalamet",
                            characterName = "Paul Atreides",
                            imagePath = null,
                            order = 0,
                            id = 12345,
                            mediaType = null,
                            releaseDate = null
                    ),
                    CastDomainModel(
                            name = "Timothée Chalamet",
                            characterName = "Paul Atreides",
                            imagePath = null,
                            order = 0,
                            id = 12345,
                            mediaType = null,
                            releaseDate = null
                    ),
                    CastDomainModel(
                            name = "Timothée Chalamet",
                            characterName = "Paul Atreides",
                            imagePath = null,
                            order = 0,
                            id = 12345,
                            mediaType = null,
                            releaseDate = null
                    ),
                    CastDomainModel(
                            name = "Timothée Chalamet",
                            characterName = "Paul Atreides",
                            imagePath = null,
                            order = 0,
                            id = 12345,
                            mediaType = null,
                            releaseDate = null
                    ),
                    CastDomainModel(
                            name = "Timothée Chalamet",
                            characterName = "Paul Atreides",
                            imagePath = null,
                            order = 0,
                            id = 12345,
                            mediaType = null,
                            releaseDate = null
                    )),
            onCastClicked = { _, _ -> },
            onViewMoreClicked = {}
    )
}
package com.aldiprahasta.tmdb.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.ui.components.BilledCastItem

@Composable
fun ContentBilledCast(
        casts: List<CastDomainModel>,
        onCastClicked: (personId: Int) -> Unit,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
                text = "Top Billed Cast",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(casts.take(10)) { item ->
                BilledCastItem(
                        name = item.name,
                        characterName = item.characterName,
                        profilePath = item.imagePath,
                        onItemClicked = {
                            onCastClicked(item.id)
                        }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun ContentBilledCastPreview() {
    ContentBilledCast(casts = listOf(
            CastDomainModel(
                    name = "Timothée Chalamet",
                    characterName = "Paul Atreides",
                    imagePath = null,
                    order = 0,
                    id = 12345,
                    mediaType = null
            ),
            CastDomainModel(
                    name = "Timothée Chalamet",
                    characterName = "Paul Atreides",
                    imagePath = null,
                    order = 0,
                    id = 12345,
                    mediaType = null
            ),
            CastDomainModel(
                    name = "Timothée Chalamet",
                    characterName = "Paul Atreides",
                    imagePath = null,
                    order = 0,
                    id = 12345,
                    mediaType = null
            ),
            CastDomainModel(
                    name = "Timothée Chalamet",
                    characterName = "Paul Atreides",
                    imagePath = null,
                    order = 0,
                    id = 12345,
                    mediaType = null
            ),
            CastDomainModel(
                    name = "Timothée Chalamet",
                    characterName = "Paul Atreides",
                    imagePath = null,
                    order = 0,
                    id = 12345,
                    mediaType = null
            )),
            onCastClicked = {}
    )
}
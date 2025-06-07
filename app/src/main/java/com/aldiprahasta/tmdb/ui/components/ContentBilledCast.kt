package com.aldiprahasta.tmdb.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.utils.getCharacterAge

private const val MAX_SHOWED_CREDITS = 10

@Composable
fun ContentBilledCast(
        sectionTitle: String,
        casts: List<CastDomainModel>,
        onCastClicked: (contentId: Int, mediaType: String?) -> Unit,
        onViewMoreClicked: () -> Unit,
        modifier: Modifier = Modifier,
        characterAgeParams: Pair<String, String>? = null
) {
    val context = LocalContext.current

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
            items(casts.take(MAX_SHOWED_CREDITS)) { item ->
                BilledCastItem(
                        name = item.name,
                        characterName = item.characterName,
                        profilePath = item.imagePath,
                        onItemClicked = {
                            onCastClicked(item.id, item.mediaType)
                        },
                        onItemLongClicked = {
                            characterAgeParams?.let { (personName, personBirthDay) ->
                                Toast.makeText(
                                        context,
                                        "$personName was ${getCharacterAge(personBirthDay, item.releaseDate)} years old in this project",
                                        Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                )
            }

            if (casts.size > MAX_SHOWED_CREDITS) {
                item {
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
private fun ContentBilledCastPreview() {
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
                            releaseDate = "",
                            totalEpisodeCount = 10
                    ),
                    CastDomainModel(
                            name = "Timothée Chalamet",
                            characterName = "Paul Atreides",
                            imagePath = null,
                            order = 0,
                            id = 12345,
                            mediaType = null,
                            releaseDate = "",
                            totalEpisodeCount = 10
                    ),
                    CastDomainModel(
                            name = "Timothée Chalamet",
                            characterName = "Paul Atreides",
                            imagePath = null,
                            order = 0,
                            id = 12345,
                            mediaType = null,
                            releaseDate = "",
                            totalEpisodeCount = 10
                    ),
                    CastDomainModel(
                            name = "Timothée Chalamet",
                            characterName = "Paul Atreides",
                            imagePath = null,
                            order = 0,
                            id = 12345,
                            mediaType = null,
                            releaseDate = "",
                            totalEpisodeCount = 10
                    ),
                    CastDomainModel(
                            name = "Timothée Chalamet",
                            characterName = "Paul Atreides",
                            imagePath = null,
                            order = 0,
                            id = 12345,
                            mediaType = null,
                            releaseDate = "",
                            totalEpisodeCount = 10
                    )),
            onCastClicked = { _, _ -> },
            onViewMoreClicked = {}
    )
}
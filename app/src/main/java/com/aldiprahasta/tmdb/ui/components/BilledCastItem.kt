package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BilledCastItem(
        name: String,
        characterName: String,
        profilePath: String?,
        onItemClicked: () -> Unit,
        modifier: Modifier = Modifier
) {
    ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = modifier
                    .size(width = 100.dp, height = 200.dp)
                    .clickable {
                        onItemClicked()
                    },
            shape = RoundedCornerShape(6.dp)
    ) {
        Column {
            ImageLoader(
                    imagePath = profilePath,
                    imageType = ImageType.PROFILE,
                    modifier = Modifier.height(120.dp)
            )
            Text(
                    text = name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                    text = characterName,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .padding(bottom = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BilledCastItemPreview() {
    BilledCastItem(
            name = "Timothée Chalamet",
            characterName = "Paul Atreides",
            profilePath = "/BE2sdjpgsa2rNTFa66f7upkaOP.jpg",
            onItemClicked = {}
    )
}
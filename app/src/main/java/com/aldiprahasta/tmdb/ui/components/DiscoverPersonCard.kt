package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
internal fun DiscoverPersonCard(
        name: String,
        subtitle: String,
        profilePath: String?,
        onClick: () -> Unit,
        trailingContent: @Composable () -> Unit = {
            Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
            )
        }
) {
    val colors = MaterialTheme.colorScheme

    Surface(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.outline),
            modifier = Modifier.fillMaxWidth()
    ) {
        Row(
                modifier = Modifier.heightIn(min = 108.dp).padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            DiscoverPersonAvatar(
                    name = name,
                    imagePath = profilePath,
                    modifier = Modifier.size(64.dp)
            )
            Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                )
                Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
            }
            trailingContent()
        }
    }
}

@Composable
internal fun DiscoverPersonAvatar(name: String, imagePath: String?, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    MovieArtwork(
            imagePath = imagePath,
            imageType = ImageType.PROFILE,
            modifier = modifier.clip(CircleShape),
            placeholder = { artworkModifier ->
                val initials = name.trim().split(Regex("\\s+")).take(2)
                        .joinToString("") { it.take(1).uppercase() }
                Box(
                        modifier = artworkModifier.background(colors.primaryContainer),
                        contentAlignment = Alignment.Center
                ) {
                    Text(initials, style = MaterialTheme.typography.titleMedium, color = colors.primary)
                }
            }
    )
}

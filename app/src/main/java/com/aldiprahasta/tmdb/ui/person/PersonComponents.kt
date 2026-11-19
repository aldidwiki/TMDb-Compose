package com.aldiprahasta.tmdb.ui.person

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PersonTopBar(title: String, onBackPressed: () -> Unit) {
    TopAppBar(
            title = { Text(title, style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.discover_back)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
internal fun PersonFavoriteButton(checked: Boolean, enabled: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val selection by animateFloatAsState(
            targetValue = if (checked) 1f else 0f,
            animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = 0.001f
            ),
            label = "Favorite selection"
    )
    val progress = selection.coerceIn(0f, 1f)
    val favoriteLabel = stringResource(R.string.person_favorite)
    val favoritedLabel = stringResource(R.string.person_favorited)
    val label = if (checked) favoritedLabel else favoriteLabel
    val shape = RoundedCornerShape(12.dp)

    Surface(
            modifier = Modifier.width(171.dp)
                    .heightIn(min = 48.dp)
                    .clip(shape)
                    .toggleable(value = checked, enabled = enabled, role = Role.Checkbox, onValueChange = onCheckedChange),
            shape = shape,
            color = lerp(colors.surface, colors.primaryContainer, progress),
            contentColor = colors.primary,
            border = BorderStroke(1.dp, lerp(colors.outline, colors.primary.copy(alpha = 0.34f), progress))
    ) {
        Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
        ) {
            Box(Modifier.size(18.dp).graphicsLayer {
                val scale = 1f + 0.32f * progress * (1f - progress)
                scaleX = scale
                scaleY = scale
            }) {
                Icon(Icons.Filled.FavoriteBorder, contentDescription = null, modifier = Modifier.matchParentSize())
                Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        modifier = Modifier.matchParentSize().graphicsLayer { alpha = progress }
                )
            }
            Box(
                    modifier = Modifier.clearAndSetSemantics { text = AnnotatedString(label) },
                    contentAlignment = Alignment.CenterStart
            ) {
                Text(
                        text = favoriteLabel,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.graphicsLayer { alpha = 1f - progress }
                )
                Text(
                        text = favoritedLabel,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.graphicsLayer { alpha = progress }
                )
            }
        }
    }
}

@Composable
internal fun PersonStatus(message: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
    Box(modifier = modifier.padding(20.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (onRetry != null) {
                TextButton(onClick = onRetry) { Text(stringResource(R.string.discover_retry)) }
            }
        }
    }
}

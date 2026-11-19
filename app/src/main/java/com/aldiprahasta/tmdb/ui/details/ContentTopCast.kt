package com.aldiprahasta.tmdb.ui.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverPersonAvatar
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview

private val TopCastCardWidth = 108.dp
private val TopCastCardPadding = 8.dp

@Composable
internal fun ContentTopCast(casts: List<CastDomainModel>, onCastClicked: (Int) -> Unit, onSeeAll: () -> Unit) {
    val topCast = remember(casts) { casts.distinctBy { it.id }.take(10) }
    val nameStyle = MaterialTheme.typography.labelMedium
    val characterStyle = MaterialTheme.typography.labelSmall
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer(cacheSize = 20)
    val labelHeight = remember(topCast, nameStyle, characterStyle, density, textMeasurer) {
        with(density) {
            val constraints = Constraints(maxWidth = TopCastCardWidth.roundToPx() - 2 * TopCastCardPadding.roundToPx())
            val height = topCast.maxOfOrNull { member ->
                val name = textMeasurer.measure(
                        member.name, nameStyle, overflow = TextOverflow.Ellipsis, maxLines = 2, constraints = constraints
                )
                val character = member.characterName.takeIf(String::isNotBlank)?.let {
                    textMeasurer.measure(
                            it, characterStyle, overflow = TextOverflow.Ellipsis, maxLines = 2, constraints = constraints
                    )
                }
                name.size.height + (character?.size?.height ?: 0)
            } ?: 0
            height.toDp()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        DetailSectionHeader(
                title = R.string.detail_top_cast,
                modifier = Modifier.padding(horizontal = 20.dp),
                onSeeAll = onSeeAll.takeIf { topCast.isNotEmpty() }
        )
        if (topCast.isEmpty()) {
            DiscoverStatus(stringResource(R.string.detail_cast_empty), Modifier.fillMaxWidth())
        } else {
            LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topCast, key = { it.id }) { member ->
                    TopCastCard(
                            member = member,
                            labelHeight = labelHeight,
                            onClick = { onCastClicked(member.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopCastCard(member: CastDomainModel, labelHeight: Dp, onClick: () -> Unit) {
    val nameStyle = MaterialTheme.typography.labelMedium
    val characterStyle = MaterialTheme.typography.labelSmall

    Surface(
            onClick = onClick,
            modifier = Modifier.width(TopCastCardWidth),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
                modifier = Modifier.fillMaxWidth().heightIn(min = 106.dp).padding(TopCastCardPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            DiscoverPersonAvatar(member.name, member.imagePath, Modifier.size(40.dp))
            Column(
                    modifier = Modifier.fillMaxWidth().height(labelHeight),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                        text = member.name,
                        textAlign = TextAlign.Center,
                        style = nameStyle,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                )
                if (member.characterName.isNotBlank()) {
                    Text(
                            text = member.characterName,
                            textAlign = TextAlign.Center,
                            style = characterStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 390)
@Composable
private fun ContentTopCastPreview() {
    DiscoverPreview {
        val actor = CastDomainModel(1, "Rebecca Ferguson", "Lady Jessica", null, 0, null, null, null)
        ContentTopCast(
                casts = listOf(actor, actor.copy(id = 2, name = "Josh Brolin", characterName = "Gurney Halleck")),
                onCastClicked = {},
                onSeeAll = {}
        )
    }
}


@Preview(widthDp = 124)
@Composable
private fun TopCastCardPreview() {
    DiscoverPreview {
        TopCastCard(
                member = CastDomainModel(1, "Rebecca Ferguson", "Lady Jessica", null, 0, null, null, null),
                labelHeight = 34.dp,
                onClick = {}
        )
    }
}

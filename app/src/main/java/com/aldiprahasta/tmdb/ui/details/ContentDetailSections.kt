package com.aldiprahasta.tmdb.ui.details

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverPersonAvatar
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.tv.TvEpisodeItem
import com.aldiprahasta.tmdb.ui.tv.tvseason.seasonLabel
import com.aldiprahasta.tmdb.ui.tv.tvseason.seasonMetadata
import com.aldiprahasta.tmdb.utils.UiState

@Composable
internal fun DetailSectionHeader(@StringRes title: Int, modifier: Modifier = Modifier, onSeeAll: (() -> Unit)? = null) {
    val label = stringResource(title)
    Row(
            modifier = modifier.fillMaxWidth().heightIn(min = 38.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f).semantics { heading() })
        if (onSeeAll != null) {
            val seeAllLabel = stringResource(R.string.detail_see_all_section, label)
            TextButton(
                    onClick = onSeeAll,
                    modifier = Modifier.semantics { onClick(label = seeAllLabel, action = null) },
                    contentPadding = PaddingValues(start = 12.dp)
            ) {
                Text(stringResource(R.string.detail_see_all), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
internal fun ContentTopCast(casts: List<CastDomainModel>, onCastClicked: (Int) -> Unit, onSeeAll: () -> Unit) {
    val topCast = remember(casts) { casts.distinctBy { it.id }.take(10) }
    val nameStyle = MaterialTheme.typography.labelMedium
    val characterStyle = MaterialTheme.typography.labelSmall
    val cardWidth = 108.dp
    val cardPadding = 8.dp
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer(cacheSize = 20)
    val labelHeight = remember(topCast, nameStyle, characterStyle, density, textMeasurer) {
        with(density) {
            val constraints = Constraints(maxWidth = cardWidth.roundToPx() - 2 * cardPadding.roundToPx())
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
                    Surface(
                            onClick = { onCastClicked(member.id) },
                            modifier = Modifier.width(cardWidth),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                                modifier = Modifier.fillMaxWidth().heightIn(min = 106.dp).padding(cardPadding),
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
            }
        }
    }
}

@Composable
internal fun ContentSeasons(
        seasons: List<TvSeasonDomainModel>,
        selectedSeason: Int?,
        seasonDetails: UiState<TvSeasonDetailDomainModel>?,
        onSeasonSelected: (Int) -> Unit,
        onSeeAll: (Int) -> Unit,
        onRetry: () -> Unit
) {
    if (seasons.isEmpty()) return

    val sortedSeasons = remember(seasons) { seasons.distinctBy { it.seasonNumber }.sortedBy { it.seasonNumber } }
    val selection = sortedSeasons.firstOrNull { it.seasonNumber == selectedSeason }
    val episodes = (seasonDetails as? UiState.Success)?.data?.tvEpisodeList.orEmpty()
    val seasonScroll = rememberLazyListState()
    LaunchedEffect(sortedSeasons, selectedSeason) {
        val index = sortedSeasons.indexOfFirst { it.seasonNumber == selectedSeason }
        val visibleItems = seasonScroll.layoutInfo.visibleItemsInfo
        if (index >= 0 && visibleItems.none { it.index == index }) seasonScroll.scrollToItem(index)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DetailSectionHeader(R.string.detail_seasons, Modifier.padding(horizontal = 20.dp))
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                        modifier = Modifier.weight(1f),
                        state = seasonScroll,
                        contentPadding = PaddingValues(start = 20.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedSeasons, key = { it.seasonNumber }) { season ->
                        FilterChip(
                                selected = season.seasonNumber == selectedSeason,
                                onClick = { onSeasonSelected(season.seasonNumber) },
                                label = { Text(seasonLabel(season), style = MaterialTheme.typography.labelMedium) },
                                shape = RoundedCornerShape(18.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                        )
                    }
                }
                selection?.let { season ->
                    Text(
                            text = seasonMetadata(season),
                            modifier = Modifier.padding(start = 10.dp, end = 20.dp).widthIn(max = 130.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End
                    )
                }
            }
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DetailSectionHeader(
                    title = R.string.detail_episodes,
                    onSeeAll = selection?.seasonNumber?.takeIf { episodes.isNotEmpty() }?.let { number ->
                        { onSeeAll(number) }
                    }
            )
            when (seasonDetails) {
                null, UiState.Loading -> LoadingScreen(Modifier.fillMaxWidth().heightIn(min = 96.dp), indicatorSizeInDp = 28.dp)
                is UiState.Error -> DiscoverStatus(stringResource(R.string.discover_load_error), Modifier.fillMaxWidth(), onRetry)
                is UiState.Success -> {
                    if (episodes.isEmpty()) {
                        DiscoverStatus(stringResource(R.string.detail_episodes_empty), Modifier.fillMaxWidth())
                    } else {
                        episodes.take(3).forEach { episode -> TvEpisodeItem(episode) }
                    }
                }
            }
        }
    }
}

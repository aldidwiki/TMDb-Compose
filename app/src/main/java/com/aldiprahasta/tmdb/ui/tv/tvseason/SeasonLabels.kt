package com.aldiprahasta.tmdb.ui.tv.tvseason

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel

@Composable
internal fun seasonLabel(season: TvSeasonDomainModel): String = season.seasonName.ifBlank {
    if (season.seasonNumber == 0) stringResource(R.string.detail_specials)
    else stringResource(R.string.detail_season_number, season.seasonNumber)
}

@Composable
internal fun seasonMetadata(season: TvSeasonDomainModel): String {
    val count = season.totalEpisodes
    val episodes = pluralStringResource(R.plurals.detail_episode_count, count, count)
    val year = season.airDateIso?.take(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) }
    return listOfNotNull(episodes, year).joinToString(" · ")
}

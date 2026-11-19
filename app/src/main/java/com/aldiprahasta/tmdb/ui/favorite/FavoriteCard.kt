package com.aldiprahasta.tmdb.ui.favorite

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.FavoriteDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverMediaCard
import com.aldiprahasta.tmdb.ui.components.DiscoverPersonCard
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import com.aldiprahasta.tmdb.utils.MediaType
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

@Composable
internal fun FavoriteCard(favorite: FavoriteDomainModel, onRemove: () -> Unit, onClick: () -> Unit) {
    val removeDescription = stringResource(R.string.favorite_remove_action, favorite.title)
    val heartColor = Color(0xFFDC4561)

    if (favorite.mediaType == MediaType.PERSON_TYPE.name) {
        val person = stringResource(R.string.search_type_person)
        DiscoverPersonCard(
                name = favorite.title,
                subtitle = favorite.knownFor?.let { stringResource(R.string.search_result_subtitle, person, it) } ?: person,
                profilePath = favorite.imagePoster,
                onClick = onClick,
                trailingContent = {
                    FilledIconButton(
                            onClick = onRemove,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = heartColor
                            )
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = removeDescription, modifier = Modifier.size(18.dp))
                    }
                }
        )
    } else {
        val isTv = favorite.mediaType == MediaType.TV_TYPE.name
        val releaseYear = favorite.releaseDate.takeLast(4).takeIf { it.length == 4 && it.all(Char::isDigit) }
        val metadata = if (isTv) {
            favorite.seasonCount?.let { pluralStringResource(R.plurals.favorite_seasons, it, it) }
        } else {
            favorite.genres?.substringBefore(',')?.takeIf { it.isNotBlank() }
        }
        DiscoverMediaCard(
                title = favorite.title,
                subtitle = listOfNotNull(releaseYear, metadata).joinToString(" · "),
                posterPath = favorite.imagePoster,
                voteAverage = favorite.voteAverage,
                description = if (isTv) stringResource(R.string.favorite_tv_series)
                else addedDateLabel(favorite.addedAt),
                onClick = onClick,
                trailingContent = {
                    IconButton(onClick = onRemove) {
                        Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = removeDescription,
                                tint = heartColor,
                                modifier = Modifier.size(18.dp)
                        )
                    }
                }
        )
    }
}

@Composable
private fun addedDateLabel(timestamp: Long): String {
    val savedDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    val today = Calendar.getInstance()
    val yesterday = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        savedDate.isSameDay(today) -> stringResource(R.string.favorite_added_today)
        savedDate.isSameDay(yesterday) -> stringResource(R.string.favorite_added_yesterday)
        else -> stringResource(
                R.string.favorite_added_date,
                DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(timestamp))
        )
    }
}

private fun Calendar.isSameDay(other: Calendar): Boolean =
        get(Calendar.YEAR) == other.get(Calendar.YEAR) && get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)

internal val favoritePreviewItem: FavoriteDomainModel
    get() = FavoriteDomainModel(
            favoriteId = 1,
            title = "Dune: Part Two",
            releaseDate = "March 1, 2024",
            imagePoster = null,
            mediaType = MediaType.MOVIE_TYPE.name,
            addedAt = 1_709_251_200_000L,
            voteAverage = 8.8,
            genres = "Science Fiction"
    )

@Preview(widthDp = 390)
@Composable
private fun FavoriteCardPreview() {
    DiscoverPreview {
        FavoriteCard(favorite = favoritePreviewItem, onRemove = {}, onClick = {})
    }
}

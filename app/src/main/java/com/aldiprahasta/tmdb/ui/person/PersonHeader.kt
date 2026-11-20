package com.aldiprahasta.tmdb.ui.person

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.ExternalIdDomainModel
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.ui.components.AnimatedFavoriteButton
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.openBrowser

@Composable
internal fun PersonHeader(
        person: PersonDomainModel,
        favorite: Boolean,
        favoriteEnabled: Boolean,
        onFavoriteChanged: (Boolean) -> Unit,
        onPhotosClicked: () -> Unit
) {
    val department = when (person.knownFor) {
        "Acting" -> stringResource(R.string.person_actor)
        "-" -> ""
        else -> person.knownFor
    }
    val location = person.placeOfBirth.substringAfterLast(',').trim()
    val subtitle = listOf(department, location).filter { it.isNotBlank() && it != "-" }.joinToString(" · ")
    val photoLabel = stringResource(R.string.person_open_photos)

    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        MovieArtwork(
                imagePath = person.profilePath,
                imageType = ImageType.PROFILE,
                modifier = Modifier.width(120.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .semantics { contentDescription = photoLabel }
                        .clickable(
                                onClickLabel = photoLabel,
                                role = Role.Button,
                                onClick = onPhotosClicked
                        )
        )
        Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                    text = person.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp, lineHeight = 34.sp)
            )
            if (subtitle.isNotEmpty()) {
                Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedFavoriteButton(
                    checked = favorite,
                    enabled = favoriteEnabled,
                    onCheckedChange = onFavoriteChanged,
                    modifier = Modifier.width(171.dp)
            )
        }
    }
}

@Composable
internal fun PersonExternalLinks(person: PersonDomainModel) {
    val context = LocalContext.current
    val instagram = person.externalIds.instragramId?.takeIf { it.isNotBlank() }
    val imdb = person.externalIds.imdbId?.takeIf { it.isNotBlank() }
    val links = listOfNotNull(
            instagram?.let { R.string.person_instagram to Constant.INSTAGRAM_BASE_URL + Uri.encode(it) },
            imdb?.let { R.string.person_imdb to Constant.IMDB_PROFILE_URL + Uri.encode(it) },
            person.name.takeIf { it.isNotBlank() }?.let {
                R.string.person_google to Constant.GOOGLE_SEARCH_BASE_URL + Uri.encode(it)
            }
    )
    if (links.isEmpty()) return

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        links.forEach { (label, url) ->
            OutlinedButton(
                    onClick = { context.openBrowser(url) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    contentPadding = PaddingValues(horizontal = 6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                        text = stringResource(label),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

internal val personPreviewSample: PersonDomainModel
    get() = PersonDomainModel(
            id = 1,
            profilePath = null,
            name = "Jessica Chastain",
            birthDay = "March 24, 1977",
            deathDay = "",
            gender = "Female",
            biography = "Jessica Chastain is an American actor and producer. She has appeared in dramas and science fiction films.",
            knownFor = "Acting",
            age = 49,
            placeOfBirth = "Sacramento, California, USA",
            externalIds = ExternalIdDomainModel("jessicachastain", null, "nm1567113", null),
            credits = listOf(
                    CastDomainModel(
                            id = 2,
                            name = "Interstellar",
                            characterName = "Murphy Cooper",
                            imagePath = null,
                            order = 0,
                            mediaType = Constant.MOVIE_MEDIA_TYPE,
                            releaseDate = "November 7, 2014",
                            totalEpisodeCount = null,
                            voteAverage = 8.5
                    )
            )
    )

@Preview(widthDp = 390)
@Composable
private fun PersonHeaderPreview() {
    DiscoverPreview {
        PersonHeader(
                person = personPreviewSample,
                favorite = true,
                favoriteEnabled = true,
                onFavoriteChanged = {},
                onPhotosClicked = {}
        )
    }
}

@Preview(widthDp = 390)
@Composable
private fun PersonExternalLinksPreview() {
    DiscoverPreview {
        PersonExternalLinks(personPreviewSample)
    }
}

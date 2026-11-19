package com.aldiprahasta.tmdb.ui.person

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.formatMovieRating
import com.aldiprahasta.tmdb.utils.getCharacterAge

@Composable
internal fun PersonKnownFor(
        person: PersonDomainModel,
        onCreditClicked: (Int, String?) -> Unit,
        onViewMoreClicked: () -> Unit,
        modifier: Modifier = Modifier
) {
    val credits = remember(person.credits) {
        person.credits.asSequence()
                .filter { it.isScreenCredit }
                .distinctBy { it.mediaType to it.id }
                .take(10)
                .toList()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.person_known_for), style = MaterialTheme.typography.titleMedium)
            if (credits.isNotEmpty()) {
                TextButton(
                        onClick = onViewMoreClicked,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(stringResource(R.string.person_see_all_credits), style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        if (credits.isEmpty()) {
            Text(
                    text = stringResource(R.string.person_no_credits),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, top = 8.dp, end = 20.dp)
            )
        } else {
            Text(
                    text = stringResource(R.string.person_known_for_subtitle, credits.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, top = 6.dp, end = 20.dp, bottom = 8.dp)
            )
            LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(credits, key = { "${it.mediaType}:${it.id}" }) { credit ->
                    KnownForCard(
                            credit = credit,
                            personName = person.name,
                            birthDay = person.birthDay,
                            onClick = { onCreditClicked(credit.id, credit.mediaType) }
                    )
                }
            }
        }
    }
}

@Composable
private fun KnownForCard(
        credit: CastDomainModel,
        personName: String,
        birthDay: String,
        onClick: () -> Unit
) {
    val context = LocalContext.current
    val media = stringResource(
            if (credit.mediaType == Constant.TV_MEDIA_TYPE) R.string.search_category_tv else R.string.search_type_movie
    )
    val releaseDate = credit.releaseDate?.takeIf { it.isNotBlank() }
    val mediaLabel = if (credit.characterName.isBlank()) media else
        stringResource(R.string.search_result_subtitle, credit.characterName, media)
    val onLongClick: (() -> Unit)? = if (birthDay.isNotBlank() && releaseDate != null) {
        {
            val age = runCatching { getCharacterAge(birthDay, releaseDate) }.getOrNull()
            if (age?.toIntOrNull()?.let { it >= 0 } == true) {
                Toast.makeText(context, context.getString(R.string.person_age_in_credit, personName, age), Toast.LENGTH_LONG).show()
            }
        }
    } else null

    Column(
            modifier = Modifier.width(140.dp)
                    .combinedClickable(role = Role.Button, onClick = onClick, onLongClick = onLongClick),
            verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(210.dp).clip(RoundedCornerShape(10.dp))) {
            MovieArtwork(
                    imagePath = credit.imagePath,
                    imageType = ImageType.POSTER,
                    modifier = Modifier.matchParentSize()
            )
            credit.voteAverage?.let { rating ->
                Text(
                        text = stringResource(R.string.discover_rating_badge, formatMovieRating(rating)),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(8.dp)
                                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
        Text(
                text = credit.name,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
        )
        Text(
                text = mediaLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

internal val CastDomainModel.isScreenCredit: Boolean
    get() = mediaType == Constant.MOVIE_MEDIA_TYPE || mediaType == Constant.TV_MEDIA_TYPE

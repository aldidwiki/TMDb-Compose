package com.aldiprahasta.tmdb.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel
import com.aldiprahasta.tmdb.domain.model.ExternalIdDomainModel
import com.aldiprahasta.tmdb.domain.model.VideoDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.ui.components.discoverHeroGradient
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview

@Composable
internal fun ContentDetailHero(detail: ContentDetailDomainModel, isTv: Boolean, onOpenPhotos: () -> Unit) {
    val scrim = MaterialTheme.colorScheme.scrim
    val genres = detail.genres.replace(", ", " / ").takeIf(String::isNotBlank)
    val seasons = detail.seasonCount?.takeIf { it > 0 }?.let {
        pluralStringResource(R.plurals.detail_season_count, it, it)
    }
    val metadata = if (isTv) listOfNotNull(detail.releaseYear, genres, seasons)
    else listOfNotNull(detail.releaseYear, genres)

    Box(
            modifier = Modifier.fillMaxWidth()
                    .heightIn(min = if (isTv) 286.dp else 300.dp)
                    .background(scrim)
                    .clickable(
                            onClickLabel = stringResource(R.string.gallery_open_backdrops),
                            role = Role.Button,
                            onClick = onOpenPhotos
                    )
    ) {
        if (!detail.backdropPath.isNullOrBlank()) {
            MovieArtwork(detail.backdropPath, ImageType.BACKDROP, Modifier.matchParentSize())
        }
        Box(Modifier.matchParentSize().background(discoverHeroGradient(scrim)))
        Column(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                    text = detail.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 26.sp, lineHeight = 32.sp),
                    color = Color.White
            )
            if (metadata.isNotEmpty()) {
                Text(
                        text = metadata.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                )
            }
        }
    }
}

internal val detailPreviewSample: ContentDetailDomainModel
    get() = ContentDetailDomainModel(
            id = 1,
            title = "Dune: Part Two",
            posterPath = null,
            backdropPath = null,
            releaseDate = "March 1, 2024",
            releaseYear = "2024",
            runtime = "2h 46m",
            runtimeMinutes = 166,
            tagline = "Long live the fighters.",
            overview = "Paul Atreides joins Chani and the Fremen on a journey across Arrakis.",
            voteAverage = 8.8,
            voteCount = 6500,
            genres = "Science Fiction, Adventure",
            certification = "PG-13",
            budget = null,
            revenue = null,
            originalLanguage = "English",
            status = "Released",
            externalId = ExternalIdDomainModel(null, null, null, null),
            casts = emptyList(),
            videos = listOf(VideoDomainModel("1", "Official Trailer", "YouTube", "Trailer", "official", true)),
            type = null,
            networks = null,
            directors = listOf("Denis Villeneuve")
    )

@Preview(widthDp = 390)
@Composable
private fun ContentDetailHeroPreview() {
    DiscoverPreview {
        ContentDetailHero(detailPreviewSample, isTv = false, onOpenPhotos = {})
    }
}

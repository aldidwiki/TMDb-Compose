package com.aldiprahasta.tmdb.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.utils.UiState

private const val GENRE_COLUMNS = 3

internal fun LazyListScope.dashboardGenres(
        genres: UiState<List<GenreDomainModel>>,
        onRetry: () -> Unit,
        onGenreClicked: (GenreDomainModel) -> Unit,
        contentInset: Dp
) {
    item(key = "genreHeading") {
        Box(modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)) {
            DashboardSectionTitle(stringResource(R.string.dashboard_browse_genres), contentInset)
        }
    }

    when (genres) {
        UiState.Loading -> item(key = "genreLoading") { DashboardSkeleton(78.dp, contentInset) }
        is UiState.Error -> item(key = "genreError") { DashboardSectionMessage(contentInset, onRetry) }
        is UiState.Success -> if (genres.data.isEmpty()) {
            item(key = "genreEmpty") { DashboardSectionMessage(contentInset) }
        } else {
            items(genres.data.chunked(GENRE_COLUMNS), key = { "genreRow-${it.first().id}" }) { row ->
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = contentInset, end = contentInset, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { genre ->
                        GenreTile(
                                genre = genre,
                                onClick = { onGenreClicked(genre) },
                                modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(GENRE_COLUMNS - row.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun GenreTile(genre: GenreDomainModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val (background, accent) = when (genre.id) {
        28 -> Color(0xFFFFF0F2) to Color(0xFFB52330)
        12, 10402 -> Color(0xFFE6F7F4) to Color(0xFF11685E)
        16, 10770 -> Color(0xFFEEF3FF) to Color(0xFF214299)
        35 -> Color(0xFFFFF7D8) to Color(0xFF785200)
        80, 9648 -> Color(0xFFF3ECFF) to Color(0xFF623C98)
        99, 10752 -> Color(0xFFEEF4E7) to Color(0xFF456239)
        18 -> Color(0xFFFFF5E5) to Color(0xFF8C5D19)
        10751 -> Color(0xFFE8F7EB) to Color(0xFF246842)
        14, 878 -> Color(0xFFEEF0FF) to Color(0xFF3B4CCA)
        36, 37 -> Color(0xFFFAF0E7) to Color(0xFF874F31)
        27, 10749 -> Color(0xFFFCEBF5) to Color(0xFF9C326C)
        else -> Color(0xFFEEF1F5) to Color(0xFF334155)
    }
    val initials = genre.name.split(' ', '-').filter { it.isNotBlank() }.let { words ->
        if (words.size > 1) words.take(2).joinToString("") { it.take(1) }.uppercase()
        else genre.name.take(2).uppercase()
    }

    Column(
            modifier = modifier
                    .heightIn(min = 78.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(background)
                    .clickable(role = Role.Button, onClick = onClick)
                    .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
                modifier = Modifier
                        .size(24.dp)
                        .background(accent.copy(alpha = 0.13f), CircleShape)
                        .clearAndSetSemantics {},
                contentAlignment = Alignment.Center
        ) {
            Text(
                    text = initials,
                    color = accent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
            )
        }
        Text(
                text = genre.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
        )
    }
}

package com.aldiprahasta.tmdb.ui.person.credits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.DiscoverTopBar
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.ui.person.isScreenCredit
import com.aldiprahasta.tmdb.ui.person.personPreviewSample
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun PersonCreditsScreen(
        personName: String,
        onBackPressed: () -> Unit,
        onCreditClicked: (Int, String?) -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: PersonCreditsViewModel = koinViewModel()
    val credits by viewModel.credits.collectAsStateWithLifecycle()

    DiscoverTheme {
        Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = { DiscoverTopBar(stringResource(R.string.person_all_credits), onBackPressed) }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Surface(color = MaterialTheme.colorScheme.surface) {
                    Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (personName.isNotBlank()) {
                            Text(personName, style = MaterialTheme.typography.headlineSmall)
                        }
                        Text(
                                text = stringResource(R.string.person_credits_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                when (val state = credits) {
                    UiState.Loading -> LoadingScreen(Modifier.fillMaxSize())
                    is UiState.Error -> DiscoverStatus(
                            message = stringResource(R.string.person_credits_error),
                            onRetry = viewModel::retry,
                            modifier = Modifier.fillMaxSize()
                    )
                    is UiState.Success -> {
                        val screenCredits = remember(state.data) { state.data.filter { it.isScreenCredit } }
                        if (screenCredits.isEmpty()) {
                            DiscoverStatus(stringResource(R.string.person_no_credits), Modifier.fillMaxSize())
                        } else {
                            LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(screenCredits) { credit ->
                                    PersonCreditCard(credit, onClick = { onCreditClicked(credit.id, credit.mediaType) })
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
private fun PersonCreditCard(credit: CastDomainModel, onClick: () -> Unit) {
    val year = credit.releaseDate?.takeLast(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) }
    val title = year?.let { stringResource(R.string.person_credit_with_year, credit.name, it) } ?: credit.name
    val isTv = credit.mediaType == Constant.TV_MEDIA_TYPE
    val type = stringResource(if (isTv) R.string.search_category_tv else R.string.search_type_movie)
    val episodes = credit.totalEpisodeCount?.takeIf { isTv && it > 0 }
    val metadata = if (episodes != null) {
        stringResource(
                R.string.search_result_subtitle,
                type,
                pluralStringResource(R.plurals.person_credit_episodes, episodes, episodes)
        )
    } else type

    Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
                modifier = Modifier.heightIn(min = 108.dp).padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            MovieArtwork(
                    imagePath = credit.imagePath,
                    imageType = ImageType.POSTER,
                    modifier = Modifier.width(56.dp).height(84.dp).clip(RoundedCornerShape(5.dp))
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                )
                if (credit.characterName.isNotBlank()) {
                    Text(
                            text = stringResource(R.string.person_credit_role, credit.characterName),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                        text = metadata,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Preview(widthDp = 390)
@Composable
private fun PersonCreditCardPreview() {
    DiscoverPreview {
        PersonCreditCard(credit = personPreviewSample.credits.first(), onClick = {})
    }
}

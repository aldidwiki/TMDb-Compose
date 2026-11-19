package com.aldiprahasta.tmdb.ui.credit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverPersonAvatar
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.DiscoverTopBar
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.UiState
import java.text.NumberFormat
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreditScreen(
        contentTitle: String,
        isTv: Boolean,
        onBackPressed: () -> Unit,
        onPersonClicked: (Int) -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: CreditViewModel = koinViewModel()
    val credits by viewModel.credits.collectAsStateWithLifecycle()

    DiscoverSystemBars()
    DiscoverTheme {
        Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = { DiscoverTopBar(stringResource(R.string.detail_full_cast), onBackPressed) }
        ) { padding ->
            val content = Modifier.fillMaxSize().padding(padding)
            when (val state = credits) {
                UiState.Loading -> LoadingScreen(content)
                is UiState.Error -> DiscoverStatus(
                        stringResource(R.string.discover_load_error), content, onRetry = viewModel::retry
                )
                is UiState.Success -> FullCastList(contentTitle, isTv, state.data, onPersonClicked, content)
            }
        }
    }
}

@Composable
private fun FullCastList(
        contentTitle: String,
        isTv: Boolean,
        cast: List<CastDomainModel>,
        onPersonClicked: (Int) -> Unit,
        modifier: Modifier = Modifier
) {
    val type = stringResource(if (isTv) R.string.detail_series_type else R.string.search_type_movie)
    LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                if (contentTitle.isNotBlank()) {
                    Text(contentTitle, style = MaterialTheme.typography.titleMedium)
                }
                Text(
                        text = stringResource(R.string.detail_cast_count, type, NumberFormat.getIntegerInstance().format(cast.size)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (cast.isEmpty()) {
            item { DiscoverStatus(stringResource(R.string.detail_cast_empty), Modifier.fillMaxWidth()) }
        } else {
            items(cast) { person ->
                FullCastCard(person = person, isTv = isTv, onClick = { onPersonClicked(person.id) })
            }
        }
    }
}

@Composable
private fun FullCastCard(person: CastDomainModel, isTv: Boolean, onClick: () -> Unit) {
    Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            color = MaterialTheme.colorScheme.surface
    ) {
        Row(
                modifier = Modifier.heightIn(min = 72.dp).padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DiscoverPersonAvatar(person.name, person.imagePath, Modifier.size(52.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(person.name, style = MaterialTheme.typography.labelLarge)
                if (person.characterName.isNotBlank()) {
                    Text(
                            text = person.characterName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                person.totalEpisodeCount?.takeIf { isTv }?.let { count ->
                    Text(
                            text = pluralStringResource(R.plurals.detail_episode_count, count, count),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

package com.aldiprahasta.tmdb.ui.credit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.DiscoverTopBar
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.UiState
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

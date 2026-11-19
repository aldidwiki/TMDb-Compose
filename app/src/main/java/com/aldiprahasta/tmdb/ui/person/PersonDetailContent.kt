package com.aldiprahasta.tmdb.ui.person

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.DiscoverTopBar
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun PersonScreen(
        personId: Int,
        onBackPressed: () -> Unit,
        onCreditClicked: (creditId: Int, mediaType: String?) -> Unit,
        onViewMoreClicked: (personName: String) -> Unit,
        onPersonImageClicked: (personId: Int, personName: String) -> Unit,
        modifier: Modifier = Modifier
) {
    val viewModel: PersonViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val favoriteError = stringResource(R.string.favorite_write_error)

    LaunchedEffect(personId) { viewModel.onEvent(PersonDetailEvent.Initialize(personId)) }
    LaunchedEffect(viewModel, favoriteError) {
        viewModel.favoriteErrors.collect { snackbarHostState.showSnackbar(favoriteError) }
    }

    DiscoverSystemBars()
    DiscoverTheme {
        Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = { DiscoverTopBar(stringResource(R.string.person_details_title), onBackPressed) },
                snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            val contentModifier = Modifier.fillMaxSize().padding(innerPadding)

            when (val person = uiState.person) {
                UiState.Loading -> LoadingScreen(contentModifier)
                is UiState.Error -> DiscoverStatus(
                        message = stringResource(R.string.person_details_error),
                        onRetry = { viewModel.onEvent(PersonDetailEvent.Retry) },
                        modifier = contentModifier
                )
                is UiState.Success -> PersonDetailContent(
                        person = person.data,
                        uiState = uiState,
                        onFavoriteChanged = { viewModel.onEvent(PersonDetailEvent.OnFavoriteClicked(it)) },
                        onPhotosClicked = { onPersonImageClicked(person.data.id, person.data.name) },
                        onViewMoreClicked = { onViewMoreClicked(person.data.name) },
                        onCreditClicked = onCreditClicked,
                        modifier = contentModifier
                )
            }
        }
    }
}

@Composable
private fun PersonDetailContent(
        person: PersonDomainModel,
        uiState: PersonDetailState,
        onFavoriteChanged: (Boolean) -> Unit,
        onPhotosClicked: () -> Unit,
        onViewMoreClicked: () -> Unit,
        onCreditClicked: (Int, String?) -> Unit,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier.verticalScroll(rememberScrollState()).padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PersonHeader(
                    person = person,
                    favorite = uiState.isFavorite,
                    favoriteEnabled = uiState.isFavoriteAvailable && !uiState.isSavingFavorite,
                    onFavoriteChanged = onFavoriteChanged,
                    onPhotosClicked = onPhotosClicked
            )
            PersonExternalLinks(person)
            PersonBiography(person.biography)
            PersonPersonalInfo(person)
        }

        PersonKnownFor(
                person = person,
                onCreditClicked = onCreditClicked,
                onViewMoreClicked = onViewMoreClicked
        )
    }
}

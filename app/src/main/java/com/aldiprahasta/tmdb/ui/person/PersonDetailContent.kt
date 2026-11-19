package com.aldiprahasta.tmdb.ui.person

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.ui.components.AnimatedFavoriteButton
import com.aldiprahasta.tmdb.ui.components.DiscoverStatus
import com.aldiprahasta.tmdb.ui.components.DiscoverTopBar
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.MovieArtwork
import com.aldiprahasta.tmdb.ui.theme.DiscoverSystemBars
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.openBrowser
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

@Composable
private fun PersonHeader(
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
private fun PersonExternalLinks(person: PersonDomainModel) {
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
                        overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PersonBiography(biography: String) {
    var expanded by rememberSaveable(biography) { mutableStateOf(false) }
    var overflowing by remember(biography) { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.person_biography), style = MaterialTheme.typography.titleMedium)
        Text(
                text = biography.takeUnless { it.isBlank() || it == "-" }
                        ?: stringResource(R.string.person_biography_unavailable),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (expanded) Int.MAX_VALUE else 6,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { if (!expanded) overflowing = it.hasVisualOverflow },
                modifier = Modifier.fillMaxWidth().animateContentSize()
        )
        if (overflowing || expanded) {
            TextButton(onClick = { expanded = !expanded }) {
                Text(stringResource(if (expanded) R.string.person_collapse else R.string.person_expand))
            }
        }
    }
}

@Composable
private fun PersonPersonalInfo(person: PersonDomainModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.person_personal_info), style = MaterialTheme.typography.titleMedium)

        Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PersonInfoField(R.string.person_known_for, person.knownFor, Modifier.weight(1f))
                    PersonInfoField(R.string.person_birth_date, person.birthDay, Modifier.weight(1f))
                }
                PersonInfoField(R.string.person_birth_place, person.placeOfBirth)
                if (person.deathDay.isNotBlank()) {
                    PersonInfoField(R.string.person_death_date, person.deathDay)
                }
                PersonInfoField(R.string.person_also_known_as, person.alsoKnownAs.joinToString("\n"))
            }
        }
    }
}

@Composable
private fun PersonInfoField(label: Int, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
                text = stringResource(label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
                text = value.takeUnless { it.isBlank() || it == "-" } ?: "—",
                style = MaterialTheme.typography.bodySmall
        )
    }
}

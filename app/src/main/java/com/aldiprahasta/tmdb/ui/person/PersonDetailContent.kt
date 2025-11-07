package com.aldiprahasta.tmdb.ui.person

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.ExternalIdDomainModel
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.ui.components.ContentBilledCast
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.ImageLoader
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
import com.aldiprahasta.tmdb.ui.components.TMDbTopBar
import com.aldiprahasta.tmdb.ui.components.rememberTopAppBarScrollBehavior
import com.aldiprahasta.tmdb.ui.details.ContentDetailExternal
import com.aldiprahasta.tmdb.utils.convertDate
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(
        personId: Int,
        onBackPressed: () -> Unit,
        onCreditClicked: (creditId: Int, mediaType: String?) -> Unit,
        onViewMoreClicked: () -> Unit,
        onPersonImageClicked: (personId: Int, personName: String) -> Unit,
        modifier: Modifier = Modifier
) {
    val personViewModel: PersonViewModel = koinViewModel()
    val uiState by personViewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehavior = rememberTopAppBarScrollBehavior()

    LaunchedEffect(personId) {
        personViewModel.onEvent(PersonDetailEvent.Initialize(personId))
    }

    Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TMDbTopBar(
                        scrollBehavior = scrollBehavior,
                        topBarTitle = {},
                        actions = {
                            val isActionEnabled = uiState.personDomainModel != null

                            IconToggleButton(
                                    enabled = isActionEnabled,
                                    checked = uiState.isFavorite,
                                    onCheckedChange = {
                                        personViewModel.onEvent(PersonDetailEvent.OnFavoriteClicked(
                                                uiState.isFavorite,
                                                uiState.personDomainModel
                                        ))
                                    }
                            ) {
                                AnimatedContent(
                                        targetState = uiState.isFavorite,
                                        label = "Animated Favorite Button",
                                        transitionSpec = { scaleIn() togetherWith scaleOut() }
                                ) { targetState ->
                                    if (targetState) {
                                        Icon(
                                                imageVector = Icons.Default.Favorite,
                                                contentDescription = null,
                                                tint = Color.Red
                                        )
                                    } else {
                                        Icon(
                                                imageVector = Icons.Default.FavoriteBorder,
                                                contentDescription = null,
                                                tint = Color.White
                                        )
                                    }
                                }
                            }
                        },
                        onBackPressed = onBackPressed,
                )
            }
    ) { innerPadding ->
        AnimatedContent(
                targetState = uiState.personDomainModel,
                label = "Animated Content",
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(tween(500))
                },
                modifier = Modifier.padding(innerPadding)
        ) { personDomainModel ->
            if (uiState.isLoading) {
                LoadingScreen()
            }

            if (uiState.personError != null) {
                ErrorScreen()
            }

            personDomainModel?.let { personDetail ->
                PersonDetailContent(
                        personDomainModel = personDetail,
                        onViewMoreClicked = onViewMoreClicked,
                        onPersonImageClicked = onPersonImageClicked,
                        onCreditClicked = { creditId, mediaType ->
                            onCreditClicked(creditId, mediaType)
                        }
                )
            }
        }
    }
}

@Composable
private fun PersonDetailContent(
        personDomainModel: PersonDomainModel,
        onCreditClicked: (creditId: Int, mediaType: String?) -> Unit,
        onViewMoreClicked: () -> Unit,
        onPersonImageClicked: (personId: Int, personName: String) -> Unit,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageLoader(
                    imagePath = personDomainModel.profilePath,
                    imageType = ImageType.PROFILE,
                    modifier = Modifier
                            .height(250.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                onPersonImageClicked(
                                        personDomainModel.id,
                                        personDomainModel.name
                                )
                            }
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                    text = personDomainModel.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(4.dp))
            ContentDetailExternal(
                    instagramId = personDomainModel.externalIds.instragramId,
                    facebookId = personDomainModel.externalIds.facebookId,
                    twitterId = personDomainModel.externalIds.twitterId,
                    imdbPair = Pair(false, personDomainModel.externalIds.imdbId),
                    googleId = personDomainModel.name
            )
            Spacer(modifier = Modifier.size(20.dp))
            PersonPersonalInfo(
                    birthDay = personDomainModel.birthDay,
                    deathDay = personDomainModel.deathDay,
                    age = personDomainModel.age,
                    placeOfBirth = personDomainModel.placeOfBirth,
                    knownFor = personDomainModel.knownFor,
                    gender = personDomainModel.gender
            )
            Spacer(modifier = Modifier.size(20.dp))
            PersonBiography(biography = personDomainModel.biography)
        }
        Spacer(modifier = Modifier.size(20.dp))
        ContentBilledCast(
                sectionTitle = "Known For",
                casts = personDomainModel.credits,
                onCastClicked = { creditId, mediaType ->
                    onCreditClicked(creditId, mediaType)
                },
                onViewMoreClicked = onViewMoreClicked,
                characterAgeParams = Pair(personDomainModel.name, personDomainModel.birthDay)
        )
    }
}

@Composable
private fun PersonPersonalInfo(
        birthDay: String,
        deathDay: String,
        age: String,
        placeOfBirth: String,
        knownFor: String,
        gender: String,
        modifier: Modifier = Modifier
) {
    val birthDayText = if (deathDay.isEmpty() && birthDay.isNotEmpty()) {
        "$birthDay ($age years old)"
    } else {
        birthDay.ifEmpty { "-" }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Personal Info", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.size(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = "Birthday",
                            style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                            text = birthDayText,
                            style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = "Known For",
                            style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                            text = knownFor,
                            style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = "Place of Birth",
                            style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                            text = placeOfBirth,
                            style = MaterialTheme.typography.bodySmall
                    )
                    if (deathDay.isNotEmpty()) {
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                                text = "Day of Death",
                                style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                                text = "$deathDay ($age years old)",
                                style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = "Gender",
                            style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                            text = gender,
                            style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonBiography(
        biography: String,
        modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showMoreButton by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
            targetValue = if (isExpanded) 180f else 0f,
            animationSpec = tween(easing = LinearEasing)
    )

    val maxLines = 5

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Biography", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.size(10.dp))
        Text(
                text = biography,
                style = MaterialTheme.typography.bodySmall,
                maxLines = if (isExpanded) Int.MAX_VALUE else maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.animateContentSize(),
                onTextLayout = { textLayoutResult ->
                    val mustShowCollapseButton = isExpanded

                    val mustShowExpandButton = !isExpanded &&
                            // Ensure the index is valid (i.e., lineCount reached maxLines)
                            textLayoutResult.lineCount == maxLines &&
                            // Check if that specific line was ellipsized
                            textLayoutResult.isLineEllipsized(maxLines - 1)

                    showMoreButton = mustShowExpandButton || mustShowCollapseButton
                }
        )

        if (showMoreButton) {
            TextButton(
                    onClick = {
                        isExpanded = !isExpanded
                    },
                    modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                )
                Text(text = if (isExpanded) "Collapse" else "Expand")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonDetailContentPreview() {
    PersonDetailContent(
            personDomainModel = PersonDomainModel(
                    id = 12345,
                    profilePath = null,
                    name = "Timothée Chalamet",
                    birthDay = "1995-12-27".convertDate(),
                    deathDay = "",
                    gender = "Male",
                    biography = "-",
                    knownFor = "Acting",
                    age = "28 years old",
                    placeOfBirth = "Manhattan, New York City, New York, USA",
                    externalIds = ExternalIdDomainModel(
                            instragramId = "",
                            facebookId = "",
                            imdbId = "",
                            twitterId = ""
                    ),
                    credits = listOf(
                            CastDomainModel(
                                    name = "Timothée Chalamet",
                                    characterName = "Paul Atreides",
                                    imagePath = null,
                                    order = 0,
                                    id = 12345,
                                    mediaType = null,
                                    releaseDate = "",
                                    totalEpisodeCount = 10
                            )
                    )
            ),
            onCreditClicked = { _, _ -> },
            onViewMoreClicked = {},
            onPersonImageClicked = { _, _ -> }
    )
}

@Preview(showBackground = true)
@Composable
private fun PersonPersonInfoPreview() {
    PersonPersonalInfo(
            birthDay = "1995-12-27".convertDate(),
            deathDay = "",
            placeOfBirth = "Manhattan, New York City, New York, USA",
            knownFor = "Acting",
            gender = "Male",
            age = "28 years old"
    )
}

@Preview(showBackground = true)
@Composable
private fun PersonBiographyPreview() {
    PersonBiography(biography = "Timothée Hal Chalamet (born December 27, 1995) is an American actor.\\n\\nHe began his career appearing in the drama series Homeland in 2012. Two years later, he made his film debut in the comedy-drama Men, Women & Children and appeared in Christopher Nolan's science fiction film Interstellar. He came into attention in Luca Guadagnino's coming-of-age film Call Me by Your Name (2017). Alongside supporting roles in Greta Gerwig's films Lady Bird (2017) and Little Women (2019), he took on starring roles in Beautiful Boy (2018) and Dune (2021)")
}
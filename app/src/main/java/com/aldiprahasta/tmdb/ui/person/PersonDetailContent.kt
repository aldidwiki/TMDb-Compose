package com.aldiprahasta.tmdb.ui.person

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import com.aldiprahasta.tmdb.ui.details.ContentDetailExternal
import com.aldiprahasta.tmdb.utils.convertDate
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(
        personId: Int,
        onBackPressed: () -> Unit,
        onCreditClicked: (creditId: Int, mediaType: String?) -> Unit,
        onViewMoreClicked: () -> Unit,
        modifier: Modifier = Modifier
) {
    val personViewModel: PersonViewModel = koinViewModel()
    personViewModel.setPersonId(personId)

    val personData by personViewModel.personDetail.collectAsStateWithLifecycle()
    val favoriteStatus by personViewModel.getFavoriteStatus.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    personViewModel.updateFavoriteState(favoriteStatus)

    Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = {
                                onBackPressed()
                            }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            }
                        },
                        actions = {
                            IconToggleButton(
                                    checked = personViewModel.isFavorite,
                                    onCheckedChange = { personViewModel.updateFavoriteState(!personViewModel.isFavorite) }
                            ) {
                                AnimatedContent(
                                        targetState = personViewModel.isFavorite,
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
                                                tint = Color.Red
                                        )
                                    }
                                }
                            }
                        },
                        scrollBehavior = scrollBehavior
                )
            }
    ) { innerPadding ->
        AnimatedContent(
                targetState = personData,
                label = "Animated Content",
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(tween(500))
                },
                modifier = Modifier.padding(innerPadding)
        ) { targetState ->
            targetState.doIfLoading {
                LoadingScreen()
            }

            targetState.doIfError { throwable, _ ->
                ErrorScreen()
            }

            targetState.doIfSuccess { personDetail ->
                if (personViewModel.isFavorite) {
                    personViewModel.addToFavorite(personDetail)
                } else {
                    personViewModel.deleteFavorite(personId)
                }

                PersonDetailContent(
                        personDomainModel = personDetail,
                        onCreditClicked = { creditId, mediaType ->
                            onCreditClicked(creditId, mediaType)
                        },
                        onViewMoreClicked = onViewMoreClicked
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
                            .width(180.dp)
                            .height(250.dp)
                            .clip(RoundedCornerShape(6.dp))
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

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Biography", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.size(10.dp))
        Text(
                text = biography,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                        .animateContentSize()
                        .height(if (isExpanded) Dp.Unspecified else 70.dp)
        )

        if (biography.length > 200) {
            TextButton(
                    onClick = {
                        isExpanded = !isExpanded
                    },
                    modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                        imageVector = if (isExpanded)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                )
                Text(text = if (isExpanded) "Collapse" else "Expand")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PersonDetailContentPreview() {
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
            onViewMoreClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PersonPersonInfoPreview() {
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
fun PersonBiographyPreview() {
    PersonBiography(biography = "Timothée Hal Chalamet (born December 27, 1995) is an American actor.\\n\\nHe began his career appearing in the drama series Homeland in 2012. Two years later, he made his film debut in the comedy-drama Men, Women & Children and appeared in Christopher Nolan's science fiction film Interstellar. He came into attention in Luca Guadagnino's coming-of-age film Call Me by Your Name (2017). Alongside supporting roles in Greta Gerwig's films Lady Bird (2017) and Little Women (2019), he took on starring roles in Beautiful Boy (2018) and Dune (2021)")
}
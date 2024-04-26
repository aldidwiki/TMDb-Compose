package com.aldiprahasta.tmdb.ui.person

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.ui.components.ErrorScreen
import com.aldiprahasta.tmdb.ui.components.ImageLoader
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.ui.components.LoadingScreen
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
        modifier: Modifier = Modifier
) {
    val personViewModel: PersonViewModel = koinViewModel()
    personViewModel.setPersonId(personId)

    val personData by personViewModel.personDetail.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

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
                        scrollBehavior = scrollBehavior
                )
            }
    ) { innerPadding ->
        personData.doIfLoading {
            LoadingScreen()
        }

        personData.doIfError { throwable, _ ->
            ErrorScreen(errorMessage = throwable.localizedMessage ?: "")
        }

        personData.doIfSuccess { personDetail ->
            PersonDetailContent(
                    personDomainModel = personDetail,
                    modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun PersonDetailContent(
        personDomainModel: PersonDomainModel,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
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
                fontWeight = FontWeight.SemiBold
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
    val birthDayText = if (deathDay.isEmpty()) {
        "$birthDay ($age years old)"
    } else {
        birthDay
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Personal Info", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.size(10.dp))
        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = "Birthday",
                        style = MaterialTheme.typography.labelMedium
                )
                Text(
                        text = birthDayText,
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

                Spacer(modifier = Modifier.size(16.dp))
                Text(
                        text = "Place of Birth",
                        style = MaterialTheme.typography.labelMedium
                )
                Text(
                        text = placeOfBirth,
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
                Spacer(modifier = Modifier.size(16.dp))
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

@Composable
private fun PersonBiography(
        biography: String,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Biography", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.size(10.dp))
        Text(
                text = biography,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Justify
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PersonDetailContentPreview() {
    PersonDetailContent(personDomainModel = PersonDomainModel(
            profilePath = null,
            name = "Timothée Chalamet",
            birthDay = "1995-12-27".convertDate(),
            deathDay = "",
            gender = "Male",
            biography = "-",
            knownFor = "Acting",
            age = "28 years old",
            placeOfBirth = "Manhattan, New York City, New York, USA"
    ))
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
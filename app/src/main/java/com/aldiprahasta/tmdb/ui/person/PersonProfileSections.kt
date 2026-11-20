package com.aldiprahasta.tmdb.ui.person

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview

@Composable
internal fun PersonBiography(biography: String) {
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
internal fun PersonPersonalInfo(person: PersonDomainModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.person_personal_info), style = MaterialTheme.typography.titleMedium)

        Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            val fieldSpacing = Arrangement.spacedBy(16.dp)
            Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = fieldSpacing
                ) {
                    PersonInfoField(R.string.person_known_for, person.knownFor)
                    PersonInfoField(R.string.person_birth_place, person.placeOfBirth)
                    PersonInfoField(R.string.person_also_known_as, person.alsoKnownAs.joinToString("\n"))
                }
                Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = fieldSpacing
                ) {
                    val hasDeathDate = person.deathDay.isNotBlank()
                    PersonInfoField(
                            label = R.string.person_birth_date,
                            value = dateWithAge(person.birthDay, person.age.takeUnless { hasDeathDate })
                    )
                    if (hasDeathDate) {
                        PersonInfoField(
                                label = R.string.person_death_date,
                                value = dateWithAge(person.deathDay, person.age)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun dateWithAge(date: String, age: Int?): String =
        if (age == null || date.isBlank()) date
        else pluralStringResource(R.plurals.person_date_with_age, age, date, age)

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

@Preview(widthDp = 390)
@Composable
private fun PersonBiographyPreview() {
    DiscoverPreview {
        PersonBiography(personPreviewSample.biography)
    }
}

@Preview(widthDp = 390)
@Composable
private fun PersonPersonalInfoPreview() {
    DiscoverPreview {
        PersonPersonalInfo(personPreviewSample)
    }
}

@Preview(widthDp = 390)
@Composable
private fun PersonPersonalInfoWithDeathDatePreview() {
    DiscoverPreview {
        PersonPersonalInfo(personPreviewSample.copy(
                birthDay = "January 10, 1950",
                deathDay = "June 05, 2017",
                age = 67
        ))
    }
}

package com.aldiprahasta.tmdb.ui.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ExploreYears
import com.aldiprahasta.tmdb.ui.components.DiscoverBottomSheet
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview
import java.util.Calendar

@Composable
internal fun ExploreYearPicker(years: ExploreYears?, onDismiss: () -> Unit, onApply: (ExploreYears?) -> Unit) {
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    DiscoverBottomSheet(
            title = stringResource(R.string.genre_release_year),
            description = stringResource(R.string.explore_year_description),
            onDismiss = onDismiss
    ) {
        ExploreYearPickerContent(years, currentYear, onDismiss, onApply)
    }
}

@Composable
private fun ColumnScope.ExploreYearPickerContent(
        years: ExploreYears?,
        currentYear: Int,
        onDismiss: () -> Unit,
        onApply: (ExploreYears?) -> Unit
) {
    var rangeMode by rememberSaveable(years) { mutableStateOf(years != null && years.from != years.to) }
    var anyYear by rememberSaveable(years) { mutableStateOf(years == null) }
    var single by rememberSaveable(years) { mutableStateOf(years?.from?.asYearText().orEmpty()) }
    var from by rememberSaveable(years) { mutableStateOf(years?.from?.asYearText().orEmpty()) }
    var to by rememberSaveable(years) { mutableStateOf(years?.to?.asYearText().orEmpty()) }

    val singleYear = single.validYear()
    val fromYear = from.validYear()
    val toYear = to.validYear()
    val invertedRange = fromYear != null && toYear != null && fromYear > toYear
    val valid = if (rangeMode) fromYear != null && toYear != null && !invertedRange else anyYear || singleYear != null

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ExploreChip(
                stringResource(R.string.explore_year_single),
                selected = !rangeMode,
                onClick = { rangeMode = false },
                modifier = Modifier.weight(1f),
                clickRole = Role.Tab
        )
        ExploreChip(
                stringResource(R.string.explore_year_range),
                selected = rangeMode,
                onClick = { rangeMode = true },
                modifier = Modifier.weight(1f),
                clickRole = Role.Tab
        )
    }
    if (rangeMode) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            YearField(
                    value = from,
                    label = stringResource(R.string.explore_year_from),
                    onValueChange = { from = it },
                    modifier = Modifier.weight(1f),
                    isError = (from.isNotEmpty() && fromYear == null) || invertedRange
            )
            YearField(
                    value = to,
                    label = stringResource(R.string.explore_year_to),
                    onValueChange = { to = it },
                    modifier = Modifier.weight(1f),
                    isError = (to.isNotEmpty() && toYear == null) || invertedRange
            )
        }
        Text(
                text = stringResource(if (invertedRange) R.string.explore_year_order_error else R.string.explore_year_range_help),
                color = if (invertedRange) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
        )
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            YearField(
                    value = single,
                    label = stringResource(R.string.explore_year_type),
                    hint = stringResource(R.string.explore_year_placeholder, currentYear),
                    onValueChange = {
                        single = it
                        anyYear = false
                    },
                    isError = !anyYear && single.isNotEmpty() && singleYear == null
            )
            if (!anyYear && singleYear == null) {
                Text(
                        stringResource(R.string.explore_year_four_digits),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Text(stringResource(R.string.explore_year_recent), style = MaterialTheme.typography.labelLarge)
        for (year in currentYear downTo (currentYear - 4)) {
            YearOption(year.asYearText(), selected = !anyYear && year == singleYear, onClick = {
                single = year.asYearText()
                anyYear = false
            })
        }
        YearOption(stringResource(R.string.explore_year_any), selected = anyYear, onClick = {
            single = ""
            anyYear = true
        })
    }
    Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
        ) { Text(stringResource(R.string.explore_year_cancel)) }
        Button(
                enabled = valid,
                onClick = {
                    val selection = if (rangeMode) ExploreYears(checkNotNull(fromYear), checkNotNull(toYear))
                    else singleYear?.let { ExploreYears(it) }
                    onApply(selection)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
        ) { Text(stringResource(R.string.explore_year_apply)) }
    }
}

@Composable
private fun YearField(
        value: String,
        label: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        hint: String? = null,
        isError: Boolean = false
) {
    OutlinedTextField(
            value = value,
            onValueChange = { text -> onValueChange(text.filter { it in '0'..'9' }.take(4)) },
            modifier = modifier.fillMaxWidth(),
            label = { Text(label) },
            placeholder = hint?.let { { Text(it) } },
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun YearOption(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().semantics {
                this.selected = selected
                role = Role.RadioButton
            },
            shape = RoundedCornerShape(12.dp),
            color = if (selected) colors.primaryContainer else colors.surface,
            border = BorderStroke(1.dp, if (selected) colors.primary else colors.outline)
    ) {
        Row(
                modifier = Modifier.heightIn(min = 38.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
            if (selected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
            } else {
                Surface(
                        modifier = Modifier.size(12.dp),
                        shape = CircleShape,
                        color = colors.surface,
                        border = BorderStroke(1.dp, colors.onSurfaceVariant)
                ) {}
            }
        }
    }
}

private fun String.validYear(): Int? =
        takeIf { it.length == 4 && it.all { character -> character in '0'..'9' } }
                ?.toIntOrNull()?.takeIf { it > 0 }


@Preview(widthDp = 390)
@Composable
private fun ExploreSingleYearPickerPreview() {
    DiscoverPreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ExploreYearPickerContent(ExploreYears(2024), currentYear = 2026, onDismiss = {}, onApply = {})
        }
    }
}

@Preview(widthDp = 390)
@Composable
private fun ExploreYearRangePickerPreview() {
    DiscoverPreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ExploreYearPickerContent(ExploreYears(2019, 2024), currentYear = 2026, onDismiss = {}, onApply = {})
        }
    }
}

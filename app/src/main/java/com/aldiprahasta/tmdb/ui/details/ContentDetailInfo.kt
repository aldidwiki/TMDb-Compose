package com.aldiprahasta.tmdb.ui.details

import androidx.annotation.StringRes
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel

@Composable
internal fun ContentDetailInfo(detail: ContentDetailDomainModel, isTv: Boolean, modifier: Modifier = Modifier) {
    val runtime = detail.runtimeMinutes?.let { stringResource(R.string.detail_runtime_minutes, it) }
    val director = detail.directors.joinToString(", ")
    val fields = (if (isTv) {
        val productionCredit = if (detail.executiveProducers.isNotEmpty()) {
            InfoField(R.string.detail_executive_producer, detail.executiveProducers.joinToString(", "))
        } else InfoField(R.string.detail_director, director)

        listOf(
                InfoField(R.string.detail_creator, detail.creators.joinToString(", ")),
                InfoField(R.string.detail_first_aired, detail.releaseDate),
                productionCredit,
                InfoField(R.string.detail_status, detail.status)
        )
    } else {
        val release = if (!detail.usReleaseDate.isNullOrBlank()) InfoField(R.string.detail_us_release, detail.usReleaseDate)
        else InfoField(R.string.detail_release, detail.releaseDate)
        listOf(
                InfoField(R.string.detail_director, director),
                release,
                InfoField(R.string.detail_runtime, runtime.orEmpty()),
                InfoField(R.string.detail_music, detail.composers.joinToString(", "))
        )
    }).filter { it.value.isNotBlank() }

    if (fields.isEmpty()) return

    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
                text = stringResource(if (isTv) R.string.detail_series_info else R.string.detail_movie_info),
                style = MaterialTheme.typography.titleMedium
        )
        Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                fields.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        row.forEach { field ->
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                        text = stringResource(field.label),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(field.value, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class InfoField(@param:StringRes val label: Int, val value: String)

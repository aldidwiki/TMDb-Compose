package com.aldiprahasta.tmdb.ui.details

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview

@Composable
internal fun DetailSectionHeader(@StringRes title: Int, modifier: Modifier = Modifier, onSeeAll: (() -> Unit)? = null) {
    val label = stringResource(title)
    Row(
            modifier = modifier.fillMaxWidth().heightIn(min = 38.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f).semantics { heading() })
        if (onSeeAll != null) {
            val seeAllLabel = stringResource(R.string.detail_see_all_section, label)
            TextButton(
                    onClick = onSeeAll,
                    modifier = Modifier.semantics { onClick(label = seeAllLabel, action = null) },
                    contentPadding = PaddingValues(start = 12.dp)
            ) {
                Text(stringResource(R.string.detail_see_all), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview(widthDp = 390)
@Composable
private fun DetailSectionHeaderPreview() {
    DiscoverPreview {
        DetailSectionHeader(title = R.string.detail_top_cast, onSeeAll = {})
    }
}

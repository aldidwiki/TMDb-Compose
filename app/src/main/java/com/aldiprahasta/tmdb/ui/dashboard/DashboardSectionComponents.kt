package com.aldiprahasta.tmdb.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.ui.components.ShimmerEffect
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview

@Composable
internal fun DashboardSectionTitle(title: String, contentInset: Dp) {
    Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = contentInset).semantics { heading() }
    )
}

@Composable
internal fun DashboardSkeleton(height: Dp, contentInset: Dp) {
    ShimmerEffect(
            modifier = Modifier
                    .padding(horizontal = contentInset)
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(12.dp))
    )
}

@Composable
internal fun DashboardSectionMessage(contentInset: Dp, onRetry: (() -> Unit)? = null) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = contentInset),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val message = if (onRetry == null) R.string.discover_no_results else R.string.discover_load_error
        Text(
                text = stringResource(message),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
        )
        if (onRetry != null) {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.discover_retry))
            }
        }
    }
}

@Preview(widthDp = 390)
@Composable
private fun DashboardSectionComponentsPreview() {
    DiscoverPreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DashboardSectionTitle("Trending", contentInset = 18.dp)
            DashboardSkeleton(height = 96.dp, contentInset = 18.dp)
            DashboardSectionMessage(contentInset = 18.dp, onRetry = {})
        }
    }
}

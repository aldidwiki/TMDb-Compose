package com.aldiprahasta.tmdb.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ContentDetailInfo(
        status: String,
        originalLanguage: String,
        budget: String,
        revenue: String,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
                text = "Details",
                style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row {
            Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
            ) {
                Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelMedium
                )
                Text(
                        text = status,
                        style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                        text = "Budget",
                        style = MaterialTheme.typography.labelMedium
                )
                Text(
                        text = budget,
                        style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
            ) {
                Text(
                        text = "Original Language",
                        style = MaterialTheme.typography.labelMedium
                )
                Text(
                        text = originalLanguage,
                        style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                        text = "Revenue",
                        style = MaterialTheme.typography.labelMedium
                )
                Text(
                        text = revenue,
                        style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentDetailInfoPreview() {
    ContentDetailInfo(
            status = "Released",
            originalLanguage = "English",
            budget = "$1,000,000.00",
            revenue = "2,000,000.00",
    )
}
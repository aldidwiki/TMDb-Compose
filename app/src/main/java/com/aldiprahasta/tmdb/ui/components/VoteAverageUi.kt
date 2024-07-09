package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aldiprahasta.tmdb.ui.theme.TMDBPrimaryColor
import com.aldiprahasta.tmdb.utils.formatVoteAverage

@Composable
fun VoteAverageUi(
        voteAverage: Double,
        modifier: Modifier = Modifier
) {
    Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                    space = 2.dp,
                    alignment = Alignment.CenterHorizontally
            ),
            modifier = modifier
                    .background(
                            color = TMDBPrimaryColor,
                            shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp)
                    .width(40.dp),
    ) {
        Image(
                imageVector = Icons.Default.Star,
                colorFilter = ColorFilter.tint(Color.White),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
        )
        Text(
                text = "${voteAverage.formatVoteAverage()}%",
                color = Color.White,
                fontSize = 10.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VoteAverageUiPreview(modifier: Modifier = Modifier) {
    VoteAverageUi(voteAverage = 8.0)
}
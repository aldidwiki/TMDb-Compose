package com.aldiprahasta.tmdb.ui.details

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.domain.model.NetworkDomainModel
import com.aldiprahasta.tmdb.ui.components.ImageLoader
import com.aldiprahasta.tmdb.ui.components.ImageType

@Composable
fun ContentDetailInfo(
        status: String,
        originalLanguage: String,
        budget: String?,
        revenue: String?,
        networks: List<NetworkDomainModel>?, // null means its from movie
        tvType: String?,
        onAllSeasonClicked: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                    text = "Details",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
            )
            networks?.let {
                TextButton(onClick = onAllSeasonClicked) {
                    Text(text = "SEE ALL SEASONS")
                }
            }
        }
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
                budget?.let {
                    Text(
                            text = "Budget",
                            style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                            text = budget,
                            style = MaterialTheme.typography.bodySmall
                    )
                }
                networks?.let { itemList ->
                    Text(
                            text = "Networks",
                            style = MaterialTheme.typography.labelMedium
                    )
                    NetworksItem(networks = itemList)
                }
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
                revenue?.let {
                    Text(
                            text = "Revenue",
                            style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                            text = revenue,
                            style = MaterialTheme.typography.bodySmall
                    )
                }
                tvType?.let {
                    Text(
                            text = "Type",
                            style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                            text = tvType,
                            style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun NetworksItem(
        networks: List<NetworkDomainModel>,
        modifier: Modifier = Modifier
) {
    val maxItemCount = 3

    Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var visibleItemCount by remember { mutableIntStateOf(maxItemCount) }
        var isExpanded by remember { mutableStateOf(false) }

        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.animateContentSize()
        ) {
            networks.take(visibleItemCount).forEach { network ->
                ImageLoader(
                        imagePath = network.networkLogoPath,
                        imageType = ImageType.LOGO,
                        modifier = Modifier
                                .padding(vertical = 4.dp)
                                .height(20.dp)
                )
            }
        }

        if (networks.size > maxItemCount) {
            TextButton(onClick = {
                visibleItemCount = if (isExpanded) {
                    maxItemCount
                } else {
                    networks.size
                }
                isExpanded = !isExpanded
            }) {
                Text(if (isExpanded) "show less" else "show more")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NetworksItemPreview() {
    NetworksItem(networks = listOf(
            NetworkDomainModel(
                    networkId = 49,
                    networkLogoPath = "/tuomPhY2UtuPTqqFnKMVHvSb724.png",
                    networkName = "HBO"
            ),
            NetworkDomainModel(
                    networkId = 49,
                    networkLogoPath = "/tuomPhY2UtuPTqqFnKMVHvSb724.png",
                    networkName = "HBO"
            ),
            NetworkDomainModel(
                    networkId = 49,
                    networkLogoPath = "/tuomPhY2UtuPTqqFnKMVHvSb724.png",
                    networkName = "HBO"
            )
    ))
}

@Preview(showBackground = true)
@Composable
private fun ContentDetailInfoPreview() {
    ContentDetailInfo(
            status = "Released",
            originalLanguage = "English",
            budget = "$1,000,000.00",
            revenue = "2,000,000.00",
            networks = null,
            tvType = null,
            onAllSeasonClicked = { }
    )
}
package com.aldiprahasta.tmdb.ui.dashboard

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.ui.theme.DiscoverTheme

internal enum class DashboardTab(@param:StringRes val label: Int) {
    HOME(R.string.dashboard_tab_home),
    EXPLORE(R.string.dashboard_tab_explore),
    SEARCH(R.string.dashboard_tab_search),
    FAVORITES(R.string.dashboard_tab_favorites)
}

@Composable
internal fun DashboardBottomNavigation(
        selectedTab: DashboardTab,
        onTabSelected: (DashboardTab) -> Unit,
        modifier: Modifier = Modifier
) {
    DiscoverTheme {
        Surface(modifier = modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.navigationBarsPadding()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Row(modifier = Modifier.fillMaxWidth().selectableGroup()) {
                    DashboardTab.entries.forEach { tab ->
                        val selected = tab == selectedTab
                        val foreground = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant

                        Column(
                                modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 59.dp)
                                        .selectable(
                                                selected = selected,
                                                onClick = { onTabSelected(tab) },
                                                role = Role.Tab
                                        ),
                                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DashboardTabIcon(tab, foreground)
                            Text(
                                    text = stringResource(tab.label),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = foreground
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardTabIcon(tab: DashboardTab, tint: Color) {
    val modifier = Modifier.size(20.dp)
    val icon = when (tab) {
        DashboardTab.HOME -> Icons.Outlined.Home
        DashboardTab.SEARCH -> Icons.Outlined.Search
        DashboardTab.FAVORITES -> Icons.Default.Favorite
        DashboardTab.EXPLORE -> null
    }

    if (icon != null) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = modifier)
    } else {
        Canvas(modifier) {
            val outlineSide = size.width * 0.62f
            val centerSide = size.width * 0.26f

            rotate(45f) {
                drawRect(
                        color = tint,
                        topLeft = Offset(center.x - outlineSide / 2, center.y - outlineSide / 2),
                        size = Size(outlineSide, outlineSide),
                        style = Stroke(1.2.dp.toPx())
                )
                drawRect(
                        color = tint,
                        topLeft = Offset(center.x - centerSide / 2, center.y - centerSide / 2),
                        size = Size(centerSide, centerSide)
                )
            }
        }
    }
}

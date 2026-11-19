package com.aldiprahasta.tmdb.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.ui.theme.DiscoverPreview

@Composable
internal fun SearchField(
        query: String,
        onQueryChanged: (String) -> Unit,
        onSearch: () -> Unit,
        modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val fieldLabel = stringResource(R.string.search_hint)

    BasicTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = modifier.fillMaxWidth().semantics { contentDescription = fieldLabel },
            singleLine = true,
            textStyle = MaterialTheme.typography.labelLarge.copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            decorationBox = { input ->
                Surface(
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, colors.outline),
                        color = colors.surface
                ) {
                    Row(
                            modifier = Modifier.heightIn(min = 48.dp).padding(start = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = colors.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                        )
                        Box(modifier = Modifier.weight(1f).padding(end = if (query.isEmpty()) 14.dp else 0.dp)) {
                            if (query.isEmpty()) {
                                Text(
                                        text = fieldLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = colors.onSurfaceVariant
                                )
                            }
                            input()
                        }
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChanged("") }) {
                                Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.search_clear),
                                        tint = colors.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
    )
}

@Preview(widthDp = 390)
@Composable
private fun SearchFieldPreview() {
    DiscoverPreview {
        SearchField(query = "tom", onQueryChanged = {}, onSearch = {})
    }
}

package com.aldiprahasta.tmdb.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalSheetGenre(
        movieGenreList: List<GenreDomainModel>,
        tvGenreList: List<GenreDomainModel>,
        selectedGenreSet: Set<GenreDomainModel>,
        showModalSheet: Boolean,
        onDismissRequest: () -> Unit,
        onFilterApplied: (selectedGenres: Set<GenreDomainModel>) -> Unit,
        modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenHeight = (configuration.screenHeightDp * 0.75).dp

    if (showModalSheet) {
        val genres = mutableListOf<GenreDomainModel>().apply {
            addAll(movieGenreList)
            addAll(tvGenreList)
        }.distinct().sortedBy { it.name }

        ModalBottomSheet(
                modifier = modifier.height(screenHeight),
                sheetState = sheetState,
                onDismissRequest = onDismissRequest
        ) {
            ModalSheetGenreContent(
                    genres = genres,
                    selectedGenreSet = selectedGenreSet,
                    onFilterApplied = {
                        onFilterApplied(it)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) onDismissRequest()
                        }
                    }
            )
        }
    }
}

@Composable
private fun ModalSheetGenreContent(
        genres: List<GenreDomainModel>,
        selectedGenreSet: Set<GenreDomainModel>,
        onFilterApplied: (selectedGenres: Set<GenreDomainModel>) -> Unit,
        modifier: Modifier = Modifier
) {
    var selectedGenres by remember { mutableStateOf((selectedGenreSet)) }

    Column(modifier = modifier) {
        Text(
                text = "Filter by Genre",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
                modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
        ) {
            items(genres) { genre ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                            checked = selectedGenres.contains(genre),
                            onCheckedChange = { selected ->
                                val currentSelected = selectedGenres.toMutableSet()
                                if (selected) {
                                    currentSelected.add(genre)
                                } else {
                                    currentSelected.remove(genre)
                                }

                                selectedGenres = currentSelected
                            }
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = genre.name)
                }
            }
        }

        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            Button(
                    modifier = Modifier.weight(1f),
                    enabled = selectedGenres.isNotEmpty(),
                    onClick = {
                        onFilterApplied(selectedGenres)
                    }
            ) {
                Text(text = "Apply Filter")
            }

            OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onFilterApplied(emptySet())
                    }
            ) {
                Text(text = "Reset Filter")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ModalSheetGenreContentPreview(modifier: Modifier = Modifier) {
    ModalSheetGenreContent(
            genres = listOf(
                    GenreDomainModel(id = 1871, name = "Otis Rosales"),
                    GenreDomainModel(id = 1871, name = "Otis Rosales"),
                    GenreDomainModel(id = 1871, name = "Otis Rosales"),
                    GenreDomainModel(id = 1871, name = "Otis Rosales"),
                    GenreDomainModel(id = 1871, name = "Otis Rosales"),
            ),
            selectedGenreSet = setOf(),
            onFilterApplied = {}
    )
}
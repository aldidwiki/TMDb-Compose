package com.aldiprahasta.tmdb.ui.credit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.CreditWrapper
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.asUiStateTriple
import com.aldiprahasta.tmdb.utils.delayAfterLoading
import com.aldiprahasta.tmdb.utils.doIfError
import com.aldiprahasta.tmdb.utils.doIfLoading
import com.aldiprahasta.tmdb.utils.doIfSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip

@OptIn(ExperimentalCoroutinesApi::class)
class CreditViewModel(private val creditWrapper: CreditWrapper) : ViewModel() {
    private val _uiState = MutableStateFlow(CreditState())
    val uiState: StateFlow<CreditState> = _uiState.asStateFlow()

    private var _isDataFetched = false

    fun onEvent(event: CreditEvent) {
        when (event) {
            is CreditEvent.Initialize -> init(
                    event.contentId,
                    event.contentType
            )

            is CreditEvent.OnGenreFilterApplied -> onGenreFilterApplied(
                    event.selectedGenres,
                    event.casts
            )

            is CreditEvent.OnSortingChipClicked -> onSortingChipClicked(
                    event.sortingComparator,
                    event.casts,
                    event.contentType,
                    event.selectedSortingChip
            )

            is CreditEvent.OnGenreFilterChipClicked -> onGenreFilterChipClicked(
                    event.selectedGenres,
                    event.casts,
                    event.selectedGenre
            )
        }
    }

    private fun onGenreFilterChipClicked(
            selectedGenres: Set<GenreDomainModel>,
            casts: List<CastDomainModel>,
            selectedGenre: GenreDomainModel
    ) {
        val newSelectedGenres = selectedGenres.toMutableSet().apply {
            remove(selectedGenre)
        }

        onGenreFilterApplied(newSelectedGenres, casts)
    }

    private fun onGenreFilterApplied(
            selectedGenres: Set<GenreDomainModel>,
            casts: List<CastDomainModel>
    ) {
        val filteredCasts = if (selectedGenres.isNotEmpty()) {
            casts.filter { cast ->
                selectedGenres.any { genre ->
                    cast.genreIds?.contains(genre.id) ?: true
                }
            }
        } else casts

        _uiState.update {
            it.copy(
                    filteredCasts = filteredCasts,
                    selectedGenres = selectedGenres
            )
        }
    }

    private fun onSortingChipClicked(
            comparator: Comparator<CastDomainModel>,
            casts: List<CastDomainModel>,
            contentType: String,
            selectedSortingChip: String
    ) {
        val sortedCasts = if (contentType != MediaType.PERSON_TYPE.name) casts.sortedWith(comparator)
        else casts

        _uiState.update {
            it.copy(
                    filteredCasts = sortedCasts,
                    selectedSortingChip = selectedSortingChip
            )
        }
    }


    private fun init(contentId: Int, contentType: String) {
        if (_isDataFetched) return
        _isDataFetched = true

        when (contentType) {
            MediaType.MOVIE_TYPE.name -> creditWrapper.getMovieCredits(contentId)
            MediaType.TV_TYPE.name -> creditWrapper.getTvCredits(contentId)
            else -> creditWrapper.getPersonCredits(contentId)// person type
        }
                .zip(creditWrapper.getMovieGenreList()) { credits, movieGenres ->
                    Pair(credits, movieGenres)
                }
                .zip(creditWrapper.getTvGenreList()) { (credits, movieGenres), tvGenres ->
                    Triple(credits, movieGenres, tvGenres)
                }.asUiStateTriple()
                .delayAfterLoading(300L)
                .onEach { state ->
                    state.doIfLoading {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    state.doIfError { throwable, errorMessage ->
                        _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    creditError = throwable,
                                    creditErrorMsg = errorMessage
                            )
                        }
                    }

                    state.doIfSuccess { (credits, movieGenres, tvGenres) ->
                        _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    filteredCasts = credits,
                                    castDomainModels = credits,
                                    movieGenreDomainModels = movieGenres,
                                    tvGenreDomainModels = tvGenres
                            )
                        }
                    }
                }.launchIn(viewModelScope)
    }
}
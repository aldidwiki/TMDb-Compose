package com.aldiprahasta.tmdb.ui.credit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.model.GenreDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.CreditWrapper
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.asUiStateTriple
import com.aldiprahasta.tmdb.utils.delayAfterLoading
import com.aldiprahasta.tmdb.utils.toStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.zip

@OptIn(ExperimentalCoroutinesApi::class)
class CreditViewModel(private val creditWrapper: CreditWrapper) : ViewModel() {
    private val creditParam = MutableStateFlow(Pair(0, MediaType.MOVIE_TYPE.name))
    fun setCreditParam(contentId: Pair<Int, String>) {
        this.creditParam.value = contentId
    }

    var selectedGenreSet by mutableStateOf(setOf<GenreDomainModel>())
        private set

    fun updateSelectedGenreSet(newSelectedGenreSet: Set<GenreDomainModel>) {
        selectedGenreSet = newSelectedGenreSet
    }

    val creditsWithGenres: StateFlow<UiState<Triple<List<CastDomainModel>, List<GenreDomainModel>, List<GenreDomainModel>>>> =
            creditParam.flatMapLatest { (contentId, contentType) ->
                when (contentType) {
                    MediaType.MOVIE_TYPE.name -> creditWrapper.getMovieCredits(contentId)
                    MediaType.TV_TYPE.name -> creditWrapper.getTvCredits(contentId)
                    else -> creditWrapper.getPersonCredits(contentId)// person type
                }
            }.zip(creditWrapper.getMovieGenreList()) { credits, movieGenres ->
                Pair(credits, movieGenres)
            }.zip(creditWrapper.getTvGenreList()) { (credits, movieGenres), tvGenres ->
                Triple(credits, movieGenres, tvGenres)
            }.asUiStateTriple().delayAfterLoading(300L).toStateFlow(viewModelScope)
}
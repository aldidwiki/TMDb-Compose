package com.aldiprahasta.tmdb.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.TvSeasonDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.DetailWrapper
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapDomainModelToEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class ContentDetailViewModel(
        private val detailWrapper: DetailWrapper,
        private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ContentDetailState())
    val uiState = _uiState.asStateFlow()
    private val favoriteErrorChannel = Channel<Unit>(Channel.CONFLATED)
    val favoriteErrors = favoriteErrorChannel.receiveAsFlow()

    private var content: ContentDetailEvent.Initialize? = null
    private var confirmedFavorite = false

    fun onEvent(event: ContentDetailEvent) {
        when (event) {
            is ContentDetailEvent.Initialize -> initialize(event)
            is ContentDetailEvent.SelectSeason -> selectSeason(event.seasonNumber)
            is ContentDetailEvent.OnFavoriteClicked -> setFavorite(event.checked)
            ContentDetailEvent.RetryDetail -> if (_uiState.value.detail is UiState.Error) loadDetail()
            ContentDetailEvent.RetrySimilar -> if (_uiState.value.similar is UiState.Error) loadSimilar()
            ContentDetailEvent.RetrySeason -> _uiState.value.selectedSeason?.let { number ->
                if (_uiState.value.seasons[number] is UiState.Error) loadSeason(number)
            }
        }
    }

    private fun initialize(event: ContentDetailEvent.Initialize) {
        if (content != null) return
        content = event
        loadDetail()
        loadSimilar()
        viewModelScope.launch {
            detailWrapper.getFavoriteStatus(event.contentId, event.mediaType.name)
                    .catch { error ->
                        Timber.e(error)
                        favoriteErrorChannel.trySend(Unit)
                    }
                    .collect { favorite ->
                        confirmedFavorite = favorite
                        _uiState.update { state ->
                            state.copy(
                                    isFavoriteAvailable = true,
                                    isFavorite = if (state.isSavingFavorite) state.isFavorite else favorite
                            )
                        }
                    }
        }
    }

    private fun loadDetail() {
        val (id, mediaType) = content ?: return
        _uiState.update { it.copy(detail = UiState.Loading) }
        viewModelScope.launch {
            val details = if (mediaType == MediaType.MOVIE_TYPE) detailWrapper.getMovieDetail(id)
            else detailWrapper.getTvDetail(id)
            details.catch { error ->
                Timber.e(error)
                emit(UiState.Error(error))
            }.collect { state ->
                _uiState.update { it.copy(detail = state) }
                if (state is UiState.Success && mediaType == MediaType.TV_TYPE) {
                    val seasons = state.data.seasons.orEmpty()
                    val restored = savedStateHandle.get<Int>(SELECTED_SEASON)
                    val selected = seasons.firstOrNull { it.seasonNumber == restored } ?: seasons.defaultSeason()
                    selected?.let { selectSeason(it.seasonNumber) }
                }
            }
        }
    }

    private fun loadSimilar() {
        val (id, mediaType) = content ?: return
        _uiState.update { it.copy(similar = UiState.Loading) }
        viewModelScope.launch {
            val result = try {
                UiState.Success(detailWrapper.getSimilarContent.getFirstPage(id, mediaType))
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Timber.e(error)
                UiState.Error(error)
            }
            _uiState.update { it.copy(similar = result) }
        }
    }

    private fun selectSeason(number: Int) {
        val state = _uiState.value
        val detail = (state.detail as? UiState.Success)?.data ?: return
        if (detail.seasons.orEmpty().none { it.seasonNumber == number }) return
        savedStateHandle[SELECTED_SEASON] = number
        _uiState.update { it.copy(selectedSeason = number) }
        if (state.seasons[number] == null) loadSeason(number)
    }

    private fun loadSeason(number: Int) {
        val contentId = content?.contentId ?: return
        _uiState.update { it.copy(seasons = it.seasons + (number to UiState.Loading)) }
        viewModelScope.launch {
            detailWrapper.getTvSeasonDetail(contentId, number)
                    .catch { error ->
                        Timber.e(error)
                        emit(UiState.Error(error))
                    }
                    .collect { season ->
                        _uiState.update { it.copy(seasons = it.seasons + (number to season)) }
                    }
        }
    }

    private fun setFavorite(checked: Boolean) {
        val (id, mediaType) = content ?: return
        val state = _uiState.value
        val detail = (state.detail as? UiState.Success)?.data ?: return
        if (!state.isFavoriteAvailable || state.isSavingFavorite || checked == state.isFavorite) return

        _uiState.update { it.copy(isFavorite = checked, isSavingFavorite = true) }
        viewModelScope.launch {
            try {
                if (checked) detailWrapper.insertFavorite(detail.mapDomainModelToEntity(mediaType.name))
                else detailWrapper.deleteFavorite(id, mediaType.name)
                confirmedFavorite = checked
                _uiState.update { it.copy(isSavingFavorite = false) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Timber.e(error)
                _uiState.update { it.copy(isFavorite = confirmedFavorite, isSavingFavorite = false) }
                favoriteErrorChannel.trySend(Unit)
            }
        }
    }

    private companion object {
        const val SELECTED_SEASON = "selected_season"
    }
}

private fun List<TvSeasonDomainModel>.defaultSeason(): TvSeasonDomainModel? {
    val today = SimpleDateFormat(Constant.SOURCE_DATE_FORMAT, Locale.US).format(Date())
    val regularSeasons = filter { it.seasonNumber > 0 }
    return regularSeasons.filter { season ->
        season.airDateIso?.let { date -> date.length == 10 && date <= today } == true
    }.maxByOrNull { it.seasonNumber }
            ?: regularSeasons.minByOrNull { it.seasonNumber }
            ?: firstOrNull()
}

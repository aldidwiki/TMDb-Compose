package com.aldiprahasta.tmdb.ui.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aldiprahasta.tmdb.domain.model.TvDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.TvWrapper
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class TvViewModel(tvWrapper: TvWrapper) : ViewModel() {
    private val tvSeasonDetailParam = MutableStateFlow<SeasonRequest?>(null)
    fun setTvSeasonDetailParam(tvId: Int, tvSeasonNumber: Int) {
        tvSeasonDetailParam.update { previous ->
            if (previous?.tvId == tvId && previous.seasonNumber == tvSeasonNumber) previous
            else SeasonRequest(tvId, tvSeasonNumber)
        }
    }

    fun retrySeason() {
        if (tvSeasonDetail.value is UiState.Error) {
            tvSeasonDetailParam.update { request -> request?.copy(attempt = request.attempt + 1) }
        }
    }

    val popularTv: StateFlow<PagingData<TvDomainModel>> = tvWrapper.getPopularTv()
            .cachedIn(viewModelScope)
            .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    PagingData.empty()
            )

    val tvSeasonDetail: StateFlow<UiState<TvSeasonDetailDomainModel>> = tvSeasonDetailParam
            .filterNotNull()
            .flatMapLatest { request ->
                tvWrapper.getTvSeasonDetail(request.tvId, request.seasonNumber).catch { error ->
                    Timber.e(error)
                    emit(UiState.Error(error))
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    private data class SeasonRequest(val tvId: Int, val seasonNumber: Int, val attempt: Int = 0)
}

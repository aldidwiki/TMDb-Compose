package com.aldiprahasta.tmdb.ui.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldiprahasta.tmdb.domain.model.TvDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.TvWrapper
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.delayAfterLoading
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class TvViewModel(tvWrapper: TvWrapper) : ViewModel() {
    private val tvSeasonDetailParam = MutableSharedFlow<Pair<Int, Int>>(1)
    fun setTvSeasonDetailParam(tvId: Int, tvSeasonNumber: Int) {
        tvSeasonDetailParam.tryEmit(Pair(tvId, tvSeasonNumber))
    }

    val popularTv: StateFlow<UiState<List<TvDomainModel>>> = tvWrapper.getPopularTv()
            .delayAfterLoading(300L)
            .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    UiState.Loading
            )

    val tvSeasonDetail: StateFlow<UiState<TvSeasonDetailDomainModel>> = tvSeasonDetailParam.flatMapLatest { pair ->
        val (tvId, tvSeasonNumber) = pair
        tvWrapper.getTvSeasonDetail(tvId, tvSeasonNumber)
    }
            .delayAfterLoading(300L)
            .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    UiState.Loading
            )

}
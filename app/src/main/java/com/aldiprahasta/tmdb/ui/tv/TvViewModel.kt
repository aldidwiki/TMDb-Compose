package com.aldiprahasta.tmdb.ui.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aldiprahasta.tmdb.domain.model.TvDomainModel
import com.aldiprahasta.tmdb.domain.model.TvSeasonDetailDomainModel
import com.aldiprahasta.tmdb.domain.usecase.wrapper.TvWrapper
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.delayAfterLoading
import com.aldiprahasta.tmdb.utils.toStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class TvViewModel(tvWrapper: TvWrapper) : ViewModel() {
    private val tvSeasonDetailParam = MutableStateFlow(Pair(0, 0))
    fun setTvSeasonDetailParam(tvId: Int, tvSeasonNumber: Int) {
        tvSeasonDetailParam.value = Pair(tvId, tvSeasonNumber)
    }

    val popularTv: StateFlow<PagingData<TvDomainModel>> = tvWrapper.getPopularTv()
            .cachedIn(viewModelScope)
            .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    PagingData.empty()
            )

    val tvSeasonDetail: StateFlow<UiState<TvSeasonDetailDomainModel>> = tvSeasonDetailParam.flatMapLatest { pair ->
        val (tvId, tvSeasonNumber) = pair
        tvWrapper.getTvSeasonDetail(tvId, tvSeasonNumber)
    }
            .delayAfterLoading(300L)
            .toStateFlow(viewModelScope)

}
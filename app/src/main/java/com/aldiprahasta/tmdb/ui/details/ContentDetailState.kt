package com.aldiprahasta.tmdb.ui.details

import androidx.compose.runtime.Immutable
import com.aldiprahasta.tmdb.domain.model.ContentDetailDomainModel

@Immutable
data class ContentDetailState(
        val isFavorite: Boolean = false,

        val contentDetailDomainModel: ContentDetailDomainModel? = null,
        val isLoading: Boolean = false,
        val contentError: Throwable? = null,
        val contentErrorMsg: String? = null
)

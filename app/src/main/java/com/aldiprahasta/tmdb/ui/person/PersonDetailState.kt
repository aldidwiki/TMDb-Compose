package com.aldiprahasta.tmdb.ui.person

import androidx.compose.runtime.Immutable
import com.aldiprahasta.tmdb.domain.model.PersonDomainModel

@Immutable
data class PersonDetailState(
        val isFavorite: Boolean = false,

        val personDomainModel: PersonDomainModel? = null,
        val isLoading: Boolean = false,
        val personError: Throwable? = null,
        val personErrorMsg: String? = null
)

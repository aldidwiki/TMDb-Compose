package com.aldiprahasta.tmdb.domain.repository

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun getPersonDetail(personId: Int): Flow<UiState<PersonResponse>>
    fun getPersonCredit(personId: Int): Flow<UiState<CreditResponse>>
}
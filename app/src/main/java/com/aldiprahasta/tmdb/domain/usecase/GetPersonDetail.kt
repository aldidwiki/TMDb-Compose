package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.domain.repository.PersonRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapPersonResponseToPersonDomainModel
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPersonDetail(private val personRepository: PersonRepository) {
    operator fun invoke(personId: Int): Flow<UiState<PersonDomainModel>> = personRepository
            .getPersonDetail(personId).map { state ->
                state.mapSuccess { response -> response.mapPersonResponseToPersonDomainModel() }
            }
}

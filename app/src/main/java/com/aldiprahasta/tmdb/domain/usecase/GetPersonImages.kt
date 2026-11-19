package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.domain.repository.PersonRepository
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapProfiles
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPersonImages(private val personRepository: PersonRepository) {
    operator fun invoke(personId: Int): Flow<UiState<List<ImageDomainModel>>> = personRepository
            .getPersonImages(personId).map { state ->
                state.mapSuccess { it.mapProfiles() }
            }
}

package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.ImageDomainModel
import com.aldiprahasta.tmdb.domain.repository.PersonRepository
import com.aldiprahasta.tmdb.ui.components.ImageType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.mapImageResponseToDomainList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPersonImages(private val personRepository: PersonRepository) {
    operator fun invoke(personId: Int): Flow<UiState<List<ImageDomainModel>>> = personRepository
            .getPersonImages(personId).map { state ->
                when (state) {
                    is UiState.Loading -> UiState.Loading

                    is UiState.Error -> UiState.Error(
                            state.throwable,
                            state.errorMessage
                    )

                    is UiState.Success -> UiState.Success(
                            state.data.mapImageResponseToDomainList(ImageType.PROFILE)
                    )
                }
            }
}
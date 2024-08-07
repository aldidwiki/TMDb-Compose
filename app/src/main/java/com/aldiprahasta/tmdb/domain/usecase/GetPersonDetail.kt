package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.PersonDomainModel
import com.aldiprahasta.tmdb.domain.repository.PersonRepository
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.convertDate
import com.aldiprahasta.tmdb.utils.mapCreditResponseToCastDomainModelList
import com.aldiprahasta.tmdb.utils.mapPersonResponseToPersonDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPersonDetail(private val personRepository: PersonRepository) {
    operator fun invoke(personId: Int): Flow<UiState<PersonDomainModel>> = personRepository
            .getPersonDetail(personId).map { state ->
                when (state) {
                    is UiState.Loading -> UiState.Loading

                    is UiState.Error -> UiState.Error(state.throwable, state.errorMessage)

                    is UiState.Success -> UiState.Success(
                            state.data.mapPersonResponseToPersonDomainModel().apply {
                                credits = state.data.combinedCredits?.mapCreditResponseToCastDomainModelList(MediaType.PERSON_TYPE)
                                        ?.sortedByDescending {
                                            it.releaseDate?.convertDate(
                                                    inFormat = Constant.APP_DATE_FORMAT,
                                                    outFormat = Constant.SORTING_DATE_FORMAT
                                            )
                                        } ?: emptyList()
                            }
                    )
                }
            }
}
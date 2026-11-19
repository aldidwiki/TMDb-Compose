package com.aldiprahasta.tmdb.domain.usecase

import com.aldiprahasta.tmdb.domain.model.CastDomainModel
import com.aldiprahasta.tmdb.domain.repository.PersonRepository
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.UiState
import com.aldiprahasta.tmdb.utils.convertDate
import com.aldiprahasta.tmdb.utils.mapCreditResponseToCastDomainModelList
import com.aldiprahasta.tmdb.utils.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPersonCredits(private val personRepository: PersonRepository) {
    operator fun invoke(personId: Int): Flow<UiState<List<CastDomainModel>>> = personRepository
            .getPersonCredit(personId).map { state ->
                state.mapSuccess { response ->
                    response.mapCreditResponseToCastDomainModelList(MediaType.PERSON_TYPE)
                            .sortedByDescending { credit ->
                                credit.releaseDate?.convertDate(
                                        inFormat = Constant.APP_DATE_FORMAT,
                                        outFormat = Constant.SORTING_DATE_FORMAT
                                )
                            }
                }
            }
}

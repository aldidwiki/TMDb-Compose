package com.aldiprahasta.tmdb.data.repository

import com.aldiprahasta.tmdb.data.source.remote.PersonRemoteDataSource
import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.domain.repository.PersonRepository
import com.aldiprahasta.tmdb.utils.UiState
import kotlinx.coroutines.flow.Flow

class PersonRepositoryImpl(private val personRemoteDataSource: PersonRemoteDataSource) : PersonRepository {
    override fun getPersonDetail(personId: Int): Flow<UiState<PersonResponse>> {
        return personRemoteDataSource.getPersonDetail(personId)
    }
}
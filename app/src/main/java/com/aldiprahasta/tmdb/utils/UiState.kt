package com.aldiprahasta.tmdb.utils

sealed class UiState<out T> {
    data class Success<out R>(val data: R) : UiState<R>()
    data class Error(val throwable: Throwable, val errorMessage: String? = null) : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
}

inline fun <T, R> UiState<T>.mapSuccess(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Error -> this
    UiState.Loading -> UiState.Loading
}

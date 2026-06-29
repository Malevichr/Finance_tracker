package ru.malevichrp.auth

sealed interface AuthUiState {
    data object Initial : AuthUiState

    data object Loading : AuthUiState
}
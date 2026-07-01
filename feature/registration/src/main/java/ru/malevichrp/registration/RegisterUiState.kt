package ru.malevichrp.registration

sealed interface RegisterUiState {
    data object Initial : RegisterUiState

    data object Loading : RegisterUiState
}
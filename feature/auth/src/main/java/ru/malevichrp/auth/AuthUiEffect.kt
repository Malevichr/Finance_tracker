package ru.malevichrp.auth

sealed interface AuthUiEffect {
    data class ShowError(val message: String) : AuthUiEffect
    data object SuccessAuth : AuthUiEffect
}
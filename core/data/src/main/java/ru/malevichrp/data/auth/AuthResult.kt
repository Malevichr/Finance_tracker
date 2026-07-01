package ru.malevichrp.data.auth

sealed interface AuthResult {
    data object Success : AuthResult
    data class Error(val exception: String) : AuthResult
}
package ru.malevichrp.data.auth

interface AuthRepository {
    suspend fun login(login: String, password: String): AuthResult
}


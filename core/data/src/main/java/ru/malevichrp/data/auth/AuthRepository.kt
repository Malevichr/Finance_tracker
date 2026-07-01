package ru.malevichrp.data.auth

interface AuthRepository {
    suspend fun login(login: String, password: String): AuthResult
    suspend fun register(
        login: String,
        email: String,
        password: String
    ): AuthResult
}


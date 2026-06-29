package ru.malevichrp.auth.data

interface AuthRepository {
    suspend fun login(login: String, password: String): AuthResult
}


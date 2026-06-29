package ru.malevichrp.auth.data

import kotlinx.coroutines.delay
import javax.inject.Inject

class AuthRepositoryFake @Inject constructor() : AuthRepository {
    private var shouldError = true
    override suspend fun login(
        login: String,
        password: String
    ): AuthResult {
        delay(500)
        return if (shouldError) {
            shouldError = false
            AuthResult.Error("Error happened")
        } else
            AuthResult.Success
    }
}
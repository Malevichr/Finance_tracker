package ru.malevichrp.data.auth

import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class AuthRepositoryFake @Inject constructor() : AuthRepository {
    private var shouldError = true
    override suspend fun login(
        login: String,
        password: String
    ): AuthResult {
        delay(500.milliseconds)
        return if (shouldError) {
            shouldError = false
            AuthResult.Error("Error happened")
        } else
            AuthResult.Success
    }
}
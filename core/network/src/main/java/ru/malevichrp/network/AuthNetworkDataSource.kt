package ru.malevichrp.network

import javax.inject.Inject

class AuthNetworkDataSource @Inject constructor(
    private val authApi: AuthApi,
) {
    suspend fun login(
        login: String,
        password: String,
    ): TokenResponse {
        return authApi.login(
            username = login,
            password = password
        )
    }
    suspend fun register(
        login: String,
        email: String,
        password: String
    ): UserResponse {
        return authApi.register(
            RegisterRequest(
                username = login,
                email = email,
                password = password,
            )
        )
    }
}
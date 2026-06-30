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
}
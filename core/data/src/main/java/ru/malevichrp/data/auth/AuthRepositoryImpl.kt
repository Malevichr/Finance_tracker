package ru.malevichrp.data.auth

import ru.malevichrp.datastore.TokenDataSource
import ru.malevichrp.model.AuthTokens
import ru.malevichrp.network.AuthNetworkDataSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthNetworkDataSource,
    private val tokenDataSource: TokenDataSource
) : AuthRepository {
    override suspend fun login(
        login: String,
        password: String
    ): AuthResult {
        return try {
            val response = authDataSource.login(login, password)
            tokenDataSource.saveTokens(
                AuthTokens(
                    response.accessToken,
                    response.refreshToken
                )
            )
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message.toString())
        }
    }

    override suspend fun register(
        login: String,
        email: String,
        password: String
    ): AuthResult {
        TODO("Not yet implemented")
    }
}
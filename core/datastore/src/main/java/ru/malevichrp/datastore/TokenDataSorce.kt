package ru.malevichrp.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.malevichrp.model.AuthTokens
import javax.inject.Inject

class TokenDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val tokens: Flow<AuthTokens?> = dataStore.data.map { preferences ->
        val accessToken = preferences[ACCESS_TOKEN]
        val refreshToken = preferences[REFRESH_TOKEN]

        if (accessToken != null && refreshToken != null) {
            AuthTokens(accessToken, refreshToken)
        } else {
            null
        }
    }

    suspend fun saveTokens(tokens: AuthTokens) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = tokens.accessToken
            preferences[REFRESH_TOKEN] = tokens.refreshToken
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}
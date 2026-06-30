package ru.malevichrp.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.malevichrp.auth.data.AuthRepository
import ru.malevichrp.auth.data.AuthRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
interface AuthDi {
    @Binds
    fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository
}
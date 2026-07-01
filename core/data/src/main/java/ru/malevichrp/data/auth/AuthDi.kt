package ru.malevichrp.data.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AuthDi {
    @Binds
    fun bindAuthRepository(repository: AuthRepositoryFake): AuthRepository
}
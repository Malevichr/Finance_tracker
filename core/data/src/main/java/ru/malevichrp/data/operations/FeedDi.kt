package ru.malevichrp.data.operations

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FeedDi {
    @Binds
    fun bindFeedRepository(
        repository: FeedRepositoryImpl,
    ): FeedRepository
}

package ru.malevichrp.network.operations

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeedNetworkModule {
    @Provides
    @Singleton
    fun provideFeedApi(
        retrofit: Retrofit,
    ): FeedApi = retrofit.create(FeedApi::class.java)
}

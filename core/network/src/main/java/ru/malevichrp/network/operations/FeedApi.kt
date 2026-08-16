package ru.malevichrp.network.operations

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface FeedApi {
    @GET("feed/")
    suspend fun getFeed(): FeedResponseDto
}

@Serializable
data class FeedResponseDto(
    val items: List<FeedItemDto>,
)

@Serializable
data class FeedItemDto(
    val id: String,
    val amount: String,
    val date: String,
    val description: String? = null,
    val category: CategoryDto? = null,
    @SerialName("entry_type")
    val entryType: String,
    val account: AccountDto? = null,
    @SerialName("account_from")
    val accountFrom: AccountDto? = null,
    @SerialName("account_to")
    val accountTo: AccountDto? = null,
    @SerialName("operations_count")
    val operationsCount: Int? = null,
)

@Serializable
data class AccountDto(
    val name: String,
)

@Serializable
data class CategoryDto(
    val name: String,
    val type: String,
)

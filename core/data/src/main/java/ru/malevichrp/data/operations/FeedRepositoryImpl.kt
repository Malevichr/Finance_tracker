package ru.malevichrp.data.operations

import kotlinx.coroutines.CancellationException
import ru.malevichrp.network.operations.FeedApi
import ru.malevichrp.network.operations.FeedItemDto
import java.time.LocalDate
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val feedApi: FeedApi,
) : FeedRepository {
    override suspend fun load(): FeedResult = try {
        FeedResult.Success(feedApi.getFeed().items.map(FeedItemDto::toDomain))
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        FeedResult.Error(exception.message.orEmpty())
    }
}

private fun FeedItemDto.toDomain(): Operation {
    val operationType = when (entryType) {
        "transfer" -> OperationType.Transfer
        else -> when (category?.type) {
            "income" -> OperationType.Income
            else -> OperationType.Expense
        }
    }
    val accountName = when (entryType) {
        "transfer" -> listOfNotNull(accountFrom?.name, accountTo?.name)
            .joinToString(separator = " → ")
            .ifBlank { null }

        else -> account?.name
    }

    return Operation(
        id = id,
        amount = amount,
        description = description,
        date = LocalDate.parse(date),
        categoryName = category?.name,
        accountName = accountName,
        type = operationType,
        chainId = null,
        operationsCount = operationsCount,
    )
}

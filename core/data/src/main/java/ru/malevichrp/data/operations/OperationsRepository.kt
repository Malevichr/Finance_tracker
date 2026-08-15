package ru.malevichrp.data.operations

import java.time.LocalDate


interface OperationsRepository {
    suspend fun load(): OperationsResult
}

sealed interface OperationsResult {
    data class Success(val list: List<Operation>) : OperationsResult
    data class Error(val exception: String) : OperationsResult
}

data class Operation(
    val id: String,
    val amount: String,
    val description: String?,
    val date: LocalDate,
    val categoryName: String?,
    val accountName: String?,
    val type: OperationType,
    val chainId: String?,
    val operationsCount: Int? = null,
)

sealed interface OperationType {
    object Income : OperationType
    object Expense : OperationType
    object Transfer : OperationType
}

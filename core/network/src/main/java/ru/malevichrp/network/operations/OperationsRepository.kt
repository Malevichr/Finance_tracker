package ru.malevichrp.network.operations

interface OperationsRepository {
    suspend fun load(): LoadResult
}
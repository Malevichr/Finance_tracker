package ru.malevichrp.feed

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.malevichrp.data.operations.Operation
import ru.malevichrp.data.operations.OperationType
import ru.malevichrp.data.operations.OperationsRepository
import ru.malevichrp.data.operations.OperationsResult
import java.time.LocalDate
import java.util.ArrayDeque

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {
    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization loads operations`() = runTest(dispatcher) {
        val operation = operation("operation")
        val repository = FakeOperationsRepository(
            OperationsResult.Success(listOf(operation)),
        )

        val viewModel = FeedViewModel(repository)
        advanceUntilIdle()

        assertEquals(1, repository.loadCalls)
        assertEquals(
            FeedUiState.Success(
                listOf(
                    FeedListItem.DateHeader("2026-08-15"),
                    operation.toOperationRow(),
                )
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun `retry performs another repository request after error`() = runTest(dispatcher) {
        val operation = operation("retry")
        val repository = FakeOperationsRepository(
            OperationsResult.Error("network failure"),
            OperationsResult.Success(listOf(operation)),
        )
        val viewModel = FeedViewModel(repository)
        advanceUntilIdle()

        assertEquals(FeedUiState.Error, viewModel.state.value)

        viewModel.retry()
        advanceUntilIdle()

        assertEquals(2, repository.loadCalls)
        assertEquals(
            FeedUiState.Success(
                listOf(
                    FeedListItem.DateHeader("2026-08-15"),
                    operation.toOperationRow(),
                )
            ),
            viewModel.state.value,
        )
    }
}

private class FakeOperationsRepository(
    vararg results: OperationsResult,
) : OperationsRepository {
    private val results = ArrayDeque(results.toList())
    var loadCalls: Int = 0
        private set

    override suspend fun load(): OperationsResult {
        loadCalls += 1
        return results.removeFirst()
    }
}

private fun operation(id: String) = Operation(
    id = id,
    amount = "100.00",
    description = "Operation $id",
    date = LocalDate.of(2026, 8, 15),
    categoryName = "Category",
    accountName = "Account",
    type = OperationType.Expense,
    chainId = null,
)

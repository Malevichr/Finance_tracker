package ru.malevichrp.feed

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.malevichrp.data.operations.FeedRepository
import ru.malevichrp.data.operations.FeedResult
import ru.malevichrp.data.operations.Operation
import ru.malevichrp.data.operations.OperationType
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
        val repository = FakeFeedRepository(
            FeedResult.Success(listOf(operation)),
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
        val repository = FakeFeedRepository(
            FeedResult.Error("network failure"),
            FeedResult.Success(listOf(operation)),
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

    @Test
    fun `two load calls - one repository load call`() = runTest(dispatcher) {
        val repository = FakeFeedRepository(
            FeedResult.Error("network failure"),
            suspendLoad = true
        )
        val viewModel = FeedViewModel(repository)

        advanceUntilIdle()
        assertEquals(FeedUiState.Loading, viewModel.state.value)
        assertEquals(1, repository.loadCalls)

        viewModel.load()
        assertEquals(FeedUiState.Loading, viewModel.state.value)
        assertEquals(1, repository.loadCalls)

        repository.returnResult()
        advanceUntilIdle()

        assertEquals(1, repository.loadCalls)
        assertEquals(
            FeedUiState.Error,
            viewModel.state.value,
        )
    }

    @Test
    fun `error result - uiEffect LoadFailed`() = runTest(dispatcher) {
        val repository = FakeFeedRepository(
            FeedResult.Error("network failure")
        )
        val viewModel = FeedViewModel(repository)
        val errorMessages = mutableListOf<FeedUiEffect>()
        backgroundScope.launch(
            start = CoroutineStart.UNDISPATCHED
        ) {
            viewModel.uiEffect.collect {
                errorMessages.add(it)
            }
        }
        runCurrent()
        assertEquals(
            FeedUiState.Error,
            viewModel.state.value,
        )

        assertEquals(listOf(FeedUiEffect.LoadFailed), errorMessages)
    }
}

private class FakeFeedRepository(
    vararg results: FeedResult,
    private val suspendLoad: Boolean = false,
) : FeedRepository {
    private val results = ArrayDeque(results.toList())
    private val gate = CompletableDeferred<Unit>()
    var loadCalls: Int = 0
        private set

    override suspend fun load(): FeedResult {
        loadCalls += 1
        if (suspendLoad)
            gate.await()
        return results.removeFirst()
    }

    fun returnResult() {
        gate.complete(Unit)
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

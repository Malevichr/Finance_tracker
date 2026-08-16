package ru.malevichrp.feed

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.malevichrp.data.operations.Operation
import ru.malevichrp.data.operations.OperationType
import java.time.LocalDate

class FeedMappingTest {
    @Test
    fun `operations are grouped by descending date with stable row order`() {
        val operations = listOf(
            operation(id = "z", date = LocalDate.of(2026, 8, 14)),
            operation(id = "b", date = LocalDate.of(2026, 8, 15)),
            operation(id = "a", date = LocalDate.of(2026, 8, 15)),
            operation(id = "nested", date = LocalDate.of(2026, 8, 16), chainId = "chain"),
        )

        val items = operations.toFeedListItems()

        assertEquals(
            listOf(
                FeedListItem.DateHeader("2026-08-15"),
                operation(id = "a", date = LocalDate.of(2026, 8, 15)).toOperationRow(),
                operation(id = "b", date = LocalDate.of(2026, 8, 15)).toOperationRow(),
                FeedListItem.DateHeader("2026-08-14"),
                operation(id = "z", date = LocalDate.of(2026, 8, 14)).toOperationRow(),
            ),
            items,
        )
    }

    @Test
    fun `top-level chain remains visible while nested operations are excluded`() {
        val chain = operation(
            id = "chain",
            date = LocalDate.of(2026, 8, 15),
            operationsCount = 3,
        )
        val nested = operation(
            id = "nested",
            date = LocalDate.of(2026, 8, 15),
            chainId = "chain",
        )

        val items = listOf(chain, nested).toFeedListItems()

        assertEquals(
            listOf(
                FeedListItem.DateHeader("2026-08-15"),
                chain.toOperationRow(),
            ),
            items,
        )
    }
}

private fun operation(
    id: String,
    date: LocalDate,
    chainId: String? = null,
    operationsCount: Int? = null,
) = Operation(
    id = id,
    amount = "100.00",
    description = "Operation $id",
    date = date,
    categoryName = "Category",
    accountName = "Account",
    type = OperationType.Expense,
    chainId = chainId,
    operationsCount = operationsCount,
)

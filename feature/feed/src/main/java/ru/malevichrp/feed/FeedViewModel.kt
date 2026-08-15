package ru.malevichrp.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.malevichrp.data.operations.Operation
import ru.malevichrp.data.operations.OperationsRepository

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: OperationsRepository,
) : ViewModel() {
    val uiMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val retry = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val _state = MutableStateFlow<FeedUiState>(FeedUiState.Success(listOf()))
    val state: StateFlow<FeedUiState> = _state

    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.load()
            result.map()
        }
    }

    fun retry() {
        retry.tryEmit(Unit)
    }

    private fun List<Operation>.toFeedListItems(): List<FeedListItem> {
        return this
            .filter { it.chainId == null }
            .sortedByDescending { it.date }
            .groupBy { it.date }
            .flatMap { (date, operations) ->
                buildList {
                    add(
                        FeedListItem.DateHeader(
                            date = date.toString(),
                        )
                    )

                    operations.forEach { operation ->
                        add(operation.toOperationRow())
                    }
                }
            }
    }
}
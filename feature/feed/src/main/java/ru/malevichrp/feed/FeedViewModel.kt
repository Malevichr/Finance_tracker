package ru.malevichrp.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.malevichrp.data.operations.FeedRepository
import ru.malevichrp.data.operations.FeedResult
import ru.malevichrp.data.operations.Operation
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository,
) : ViewModel() {
    private val _uiEffect = MutableSharedFlow<FeedUiEffect>(extraBufferCapacity = 1)
    val uiEffect: SharedFlow<FeedUiEffect> = _uiEffect.asSharedFlow()

    private val _state = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val state: StateFlow<FeedUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch {
            _state.value = FeedUiState.Loading
            when (val result = repository.load()) {
                is FeedResult.Success -> {
                    _state.value = FeedUiState.Success(result.list.toFeedListItems())
                }

                is FeedResult.Error -> {
                    _state.value = FeedUiState.Error
                    _uiEffect.emit(FeedUiEffect.LoadFailed)
                }
            }
        }
    }

    fun retry() {
        load()
    }
}

internal fun List<Operation>.toFeedListItems(): List<FeedListItem> =
    asSequence()
        .filter { it.chainId == null }
        .sortedWith(compareByDescending<Operation> { it.date }.thenBy { it.id })
        .groupBy { it.date }
        .flatMap { (date, operations) ->
            buildList {
                add(FeedListItem.DateHeader(date = date.toString()))
                operations.mapTo(this) { it.toOperationRow() }
            }
        }

internal fun Operation.toOperationRow(): FeedListItem.OperationRow {
    val title = description
        ?.takeIf(String::isNotBlank)
        ?: accountName
        ?: categoryName
        .orEmpty()

    return FeedListItem.OperationRow(
        title = title,
        category = categoryName.orEmpty().takeUnless { it == title }.orEmpty(),
        amount = amount,
        id = id,
    )
}

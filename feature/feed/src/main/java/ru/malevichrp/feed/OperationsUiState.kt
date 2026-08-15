package ru.malevichrp.feed

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Success(val data: List<FeedListItem>) : FeedUiState
    data object Error : FeedUiState
}
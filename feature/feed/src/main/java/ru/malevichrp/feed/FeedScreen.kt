package ru.malevichrp.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle

@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onBackClick: () -> Unit,
    navigateDetails: (String) -> Unit,
    showMessage: (String) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val loadErrorMessage = stringResource(R.string.feed_load_error)
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    FeedUiEffect.LoadFailed -> showMessage(loadErrorMessage)
                }
            }
        }
    }
    FeedScreenUi(
        state.value,
        onBackClick = onBackClick,
        onRetryClick = viewModel::retry,
        navigateDetails = navigateDetails
    )
}

@Composable
fun FeedScreenUi(
    state: FeedUiState,
    onBackClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    navigateDetails: (String) -> Unit = {}
) {
    Scaffold(
        Modifier.padding(8.dp),
        topBar = {
            BackButton(onBackClick)
        }
    ) { paddingValues ->
        when (state) {
            is FeedUiState.Success ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues)
                ) {
                    items(
                        items = state.data,
                        key = { it.key }
                    ) { item ->
                        when (item) {
                            is FeedListItem.DateHeader -> DateCard(item)
                            is FeedListItem.OperationRow -> OperationCard(
                                item,
                                navigateDetails
                            )
                        }
                    }
                }

            is FeedUiState.Error -> RetryComponent(
                onRetryClick,
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
            )

            is FeedUiState.Loading -> LoadingComponent(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
            )
        }
    }
}

sealed interface FeedListItem {
    val key: String

    data class OperationRow(
        val title: String,
        val category: String,
        val amount: String,
        val id: String
    ) : FeedListItem {
        override val key: String = "operation:$id"
    }

    data class DateHeader(val date: String) : FeedListItem {
        override val key: String = "dateheader:$date"
    }
}

@Composable
fun DateCard(item: FeedListItem.DateHeader) {
    Text(
        text = item.date,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            start = 16.dp,
            top = 16.dp,
            bottom = 8.dp
        )
    )
}

@Composable
fun OperationCard(
    transactionData: FeedListItem.OperationRow,
    navigateDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { navigateDetails(transactionData.id) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1F)) {
            Text(
                transactionData.title,
                style = MaterialTheme.typography.titleMedium
            )

            if (transactionData.category.isNotBlank()) {
                Text(transactionData.category)
            }

        }
        Text(
            transactionData.amount,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

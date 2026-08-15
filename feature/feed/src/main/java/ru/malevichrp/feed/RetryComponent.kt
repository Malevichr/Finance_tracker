package ru.malevichrp.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RetryComponent(onClick: () -> Unit, modifier: Modifier = Modifier, iconSize: Dp = 64.dp) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.Center)
                .size(iconSize)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.retry_load),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
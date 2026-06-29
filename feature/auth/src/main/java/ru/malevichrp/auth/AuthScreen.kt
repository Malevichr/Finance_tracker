package ru.malevichrp.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onSuccessLogin: () -> Unit,
    onRegisterClick: () -> Unit,
    showErrorMessage: (String) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val loginTextFieldState = rememberTextFieldState()
    val passwordTextFieldState = rememberTextFieldState()

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEffect.collect { state ->
                when (state) {
                    is AuthUiEffect.SuccessAuth -> {
                        onSuccessLogin()
                    }

                    is AuthUiEffect.ShowError -> {
                        showErrorMessage(state.message)
                    }
                }
            }
        }
    }
    AuthScreenUi(
        loginTextFieldState,
        passwordTextFieldState,
        state.value,
        {
            viewModel.login(
                loginTextFieldState.text.toString(),
                passwordTextFieldState.text.toString()
            )
        },
        onRegisterClick
    )
}

@Composable
internal fun AuthScreenUi(
    loginTextFieldState: TextFieldState,
    passwordTextFieldState: TextFieldState,
    state: AuthUiState,
    onEnterClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    val enabledButtons = state is AuthUiState.Initial
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.entrance),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(96.dp))
        OutlinedTextField(
            loginTextFieldState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = {
                Text(stringResource(R.string.login))
            }
        )
        OutlinedTextField(
            passwordTextFieldState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = {
                Text(stringResource(R.string.password))
            }
        )
        Spacer(Modifier.height(96.dp))

        Button(onEnterClick, enabled = enabledButtons) {
            when (state) {
                AuthUiState.Initial -> Text(stringResource(R.string.enter))
                AuthUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
            }
        }
        Button(onRegisterClick, enabled = enabledButtons) {
            Text(stringResource(R.string.registration))
        }
    }
}

@Preview
@Composable
fun AuthScreenPreviewInitial() {
    AuthScreenUi(
        loginTextFieldState = rememberTextFieldState("Login"),
        passwordTextFieldState = rememberTextFieldState("Password"),
        state = AuthUiState.Initial,
        onEnterClick = {},
        {}
    )
}

@Preview
@Composable
fun AuthScreenPreviewLoading() {
    AuthScreenUi(
        loginTextFieldState = rememberTextFieldState("Login"),
        passwordTextFieldState = rememberTextFieldState("Password"),
        state = AuthUiState.Loading,
        onEnterClick = {},
        {}
    )
}


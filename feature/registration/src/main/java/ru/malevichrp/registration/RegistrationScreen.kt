package ru.malevichrp.registration

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onSuccessRegister: () -> Unit,
    onBackClick: () -> Unit,
    showMessage: (String) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val loginTextFieldState = rememberTextFieldState()
    val emailTextFieldState = rememberTextFieldState()
    val passwordTextFieldState = rememberTextFieldState()

    val successMessage = stringResource(R.string.successful_registration)

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEffect.collect { state ->
                when (state) {
                    is RegisterUiEffect.SuccessRegister -> {
                        showMessage(successMessage)
                        onSuccessRegister()
                    }

                    is RegisterUiEffect.ShowError -> {
                        showMessage(state.message)
                    }
                }
            }
        }
    }
    RegisterScreenUi(
        loginTextFieldState,
        emailTextFieldState,
        passwordTextFieldState,
        state.value,
        {
            viewModel.register(
                loginTextFieldState.text.toString(),
                emailTextFieldState.text.toString(),
                passwordTextFieldState.text.toString()
            )
        },
        onBackClick
    )
}

@Composable
internal fun RegisterScreenUi(
    loginTextFieldState: TextFieldState,
    emailTextFieldState: TextFieldState,
    passwordTextFieldState: TextFieldState,
    state: RegisterUiState,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val enabledButtons = state is RegisterUiState.Initial
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.registaration),
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
            emailTextFieldState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = {
                Text(stringResource(R.string.email))
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

        Button(onRegisterClick, enabled = enabledButtons) {
            when (state) {
                RegisterUiState.Initial -> Text(stringResource(R.string.register))
                RegisterUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
            }
        }
        Button(onBackClick, enabled = enabledButtons) {
            Text(stringResource(R.string.back))
        }
    }
}
package ru.malevichrp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.malevichrp.data.auth.AuthRepository
import ru.malevichrp.data.auth.AuthResult
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val state: StateFlow<AuthUiState> = _state

    private val _uiEffect = MutableSharedFlow<AuthUiEffect>(
        extraBufferCapacity = 1
    )
    val uiEffect = _uiEffect.asSharedFlow()

    fun login(login: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { AuthUiState.Loading }
            val result = repository.login(login, password)
            withContext(Dispatchers.Main) {
                when (result) {
                    is AuthResult.Success -> _uiEffect.tryEmit(AuthUiEffect.SuccessAuth)
                    is AuthResult.Error -> {
                        _state.update { AuthUiState.Initial }
                        _uiEffect.tryEmit(
                            AuthUiEffect.ShowError(result.exception)
                        )
                    }
                }
            }
        }
    }
}


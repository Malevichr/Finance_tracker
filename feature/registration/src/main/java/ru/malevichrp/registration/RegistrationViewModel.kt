package ru.malevichrp.registration

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
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow<RegisterUiState>(RegisterUiState.Initial)
    val state: StateFlow<RegisterUiState> = _state

    private val _uiEffect = MutableSharedFlow<RegisterUiEffect>(
        extraBufferCapacity = 1
    )
    val uiEffect = _uiEffect.asSharedFlow()

    fun register(login: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { RegisterUiState.Loading }
            val result = repository.register(login, email, password)
            withContext(Dispatchers.Main) {
                when (result) {
                    is AuthResult.Success -> _uiEffect.tryEmit(RegisterUiEffect.SuccessRegister)
                    is AuthResult.Error -> {
                        _state.update { RegisterUiState.Initial }
                        _uiEffect.tryEmit(
                            RegisterUiEffect.ShowError(result.exception)
                        )
                    }
                }
            }
        }
    }
}



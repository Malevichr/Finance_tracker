package ru.malevichrp.registration

sealed interface RegisterUiEffect {
    data class ShowError(val message: String) : RegisterUiEffect
    data object SuccessRegister : RegisterUiEffect
}
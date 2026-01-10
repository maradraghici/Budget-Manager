package com.example.budgetmanager.ui.screen.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SignUpState(
    val username: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val secondPassword: String = "",
    val passwordVisibility: Boolean = false,
    val secondPasswordVisibility: Boolean = false,
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface SignUpEvent {
    data class UsernameChanged(val username: String) : SignUpEvent
    data class PhoneNumberChanged(val phoneNumber: String) : SignUpEvent
    data class PasswordChanged(val password: String) : SignUpEvent
    data class SecondPasswordChanged(val password: String) : SignUpEvent
    data object TogglePasswordVisibility : SignUpEvent
    data object ToggleSecondPasswordVisibility : SignUpEvent
    data object TermsAcceptedChanged : SignUpEvent
    data object SignUpClicked : SignUpEvent

    data object OnLoginClick : SignUpEvent
}

sealed interface SignUpEffect {
    data object OnSignUpSuccess : SignUpEffect
    data object OnLoginClick : SignUpEffect
}

class SignUpViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SignUpEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.UsernameChanged -> {
                _state.value = _state.value.copy(username = event.username)
            }
            is SignUpEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }
            is SignUpEvent.SecondPasswordChanged -> {
                _state.value = _state.value.copy(secondPassword = event.password)
            }
            is SignUpEvent.PhoneNumberChanged -> {
                _state.value = _state.value.copy(phoneNumber = event.phoneNumber)
            }
            is SignUpEvent.TogglePasswordVisibility -> {
                _state.value = _state.value.copy(passwordVisibility = !_state.value.passwordVisibility)
            }
            is SignUpEvent.ToggleSecondPasswordVisibility -> {
                _state.value = _state.value.copy(secondPasswordVisibility = !_state.value.secondPasswordVisibility)
            }
            is SignUpEvent.TermsAcceptedChanged -> {
                _state.value = _state.value.copy(termsAccepted = !_state.value.termsAccepted)
            }
            is SignUpEvent.SignUpClicked -> {
                // Handle signup logic here
                //...

                viewModelScope.launch {
                    _effect.emit(SignUpEffect.OnSignUpSuccess)
                }

            }

            is SignUpEvent.OnLoginClick -> {
                viewModelScope.launch {
                    _effect.emit(SignUpEffect.OnLoginClick)
                }
            }
        }
    }
}
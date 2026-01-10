package com.example.budgetmanager.ui.screen.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val phoneNumber: String = "",
    val password: String = "",
    val passwordVisibility: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface LoginEvent {
    data class PhoneNumberChanged(val phoneNumber: String) : LoginEvent
    data class PasswordChanged(val password: String) : LoginEvent
    data object TogglePasswordVisibility : LoginEvent
    data object LoginClicked : LoginEvent

    data object OnSignUpClick : LoginEvent
}

sealed interface LoginEffect {
    data object OnLoginSuccess : LoginEffect
    data object OnSignUpClick : LoginEffect
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }
            is LoginEvent.PhoneNumberChanged -> {
                _state.value = _state.value.copy(phoneNumber = event.phoneNumber)
            }
            is LoginEvent.TogglePasswordVisibility -> {
                _state.value = _state.value.copy(passwordVisibility = !_state.value.passwordVisibility)
            }
            is LoginEvent.LoginClicked -> {
                // Handle login logic here
                //...

                viewModelScope.launch {
                    UserPreferences.saveUserId(context, 1)
                    _effect.emit(LoginEffect.OnLoginSuccess)
                }

            }

            is LoginEvent.OnSignUpClick -> {
                viewModelScope.launch {
                    _effect.emit(LoginEffect.OnSignUpClick)
                }
            }
        }
    }
}
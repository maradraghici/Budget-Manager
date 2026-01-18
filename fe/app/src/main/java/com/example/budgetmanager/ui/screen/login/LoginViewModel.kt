package com.example.budgetmanager.ui.screen.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.UserPreferences
import com.example.budgetmanager.data.remote.dto.LoginRequest
import com.example.budgetmanager.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val username: String = "",
    val password: String = "",
    val passwordVisibility: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface LoginEvent {
    data class UsernameChanged(val username: String) : LoginEvent
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
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository
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
            is LoginEvent.UsernameChanged -> {
                _state.value = _state.value.copy(username = event.username)
            }
            is LoginEvent.TogglePasswordVisibility -> {
                _state.value = _state.value.copy(passwordVisibility = !_state.value.passwordVisibility)
            }
            is LoginEvent.LoginClicked -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)

                    try{
                        val request = LoginRequest(
                            username = _state.value.username,
                            password = _state.value.password
                        )

                        val response = authRepository.loginUser(request)
                        if (response.isSuccessful && response.body() != null) {
                            val responseBody = response.body()!!
                            UserPreferences.saveUserId(context, responseBody.userId)
                            UserPreferences.saveToken(context, responseBody.token)
                            _effect.emit(LoginEffect.OnLoginSuccess)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        _state.value = _state.value.copy(isLoading = false)
                    }
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
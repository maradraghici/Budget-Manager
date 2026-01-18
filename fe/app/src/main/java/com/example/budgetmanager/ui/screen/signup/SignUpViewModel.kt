package com.example.budgetmanager.ui.screen.signup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.UserPreferences
import com.example.budgetmanager.data.remote.dto.RegisterRequest
import com.example.budgetmanager.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class SignUpViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository
) : ViewModel() {
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
                viewModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)

                    try{
                        val request = RegisterRequest(
                            username = _state.value.username,
                            phoneNumber = _state.value.phoneNumber,
                            password = _state.value.password
                        )

                        val response = authRepository.registerUser(request)
                        if (response.isSuccessful && response.body() != null) {
                            val responseBody = response.body()!!
                            UserPreferences.saveUserId(context, responseBody.userId)
                            UserPreferences.saveToken(context, responseBody.token)
                            _effect.emit(SignUpEffect.OnSignUpSuccess)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        _state.value = _state.value.copy(isLoading = false)
                    }
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
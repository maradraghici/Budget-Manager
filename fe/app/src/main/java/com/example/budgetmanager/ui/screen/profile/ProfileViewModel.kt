package com.example.budgetmanager.ui.screen.profile

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.UserPreferences
import com.example.budgetmanager.data.remote.dto.UserResponse
import com.example.budgetmanager.data.remote.dto.UserUpdateRequest
import com.example.budgetmanager.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ProfileState(
    val user: UserResponse = UserResponse("", ""),
    val username: String = "",
    val phoneNumber: String = "",
    val oldPassword: String = "",
    val password: String = "",
    val secondPassword: String = "",
    val oldPasswordVisibility: Boolean = false,
    val passwordVisibility: Boolean = false,
    val secondPasswordVisibility: Boolean = false,
    val imageUri: Uri? = null,
    val isLoading: Boolean = false
)

sealed interface ProfileEvent {
    data class UsernameChanged(val username: String) : ProfileEvent
    data class PhoneNumberChanged(val phoneNumber: String) : ProfileEvent
    data class OldPasswordChanged(val password: String) : ProfileEvent
    data class PasswordChanged(val password: String) : ProfileEvent
    data class SecondPasswordChanged(val password: String) : ProfileEvent
    data object ToggleOldPasswordVisibility : ProfileEvent
    data object TogglePasswordVisibility : ProfileEvent
    data object ToggleSecondPasswordVisibility : ProfileEvent
    data class ProfileImageChanged(val uri: Uri?) : ProfileEvent
    data object OnConfirmClick : ProfileEvent
    data object OnSignOutClick : ProfileEvent
}

sealed interface ProfileEffect {
    data object OnSignOutClick : ProfileEffect
    data class OnProfileChanged(val message: String) : ProfileEffect
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.UsernameChanged -> {
                _state.value = _state.value.copy(username = event.username)
            }

            is ProfileEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }

            is ProfileEvent.SecondPasswordChanged -> {
                _state.value = _state.value.copy(secondPassword = event.password)
            }

            is ProfileEvent.PhoneNumberChanged -> {
                _state.value = _state.value.copy(phoneNumber = event.phoneNumber)
            }

            is ProfileEvent.TogglePasswordVisibility -> {
                _state.value =
                    _state.value.copy(passwordVisibility = !_state.value.passwordVisibility)
            }
            is ProfileEvent.ToggleSecondPasswordVisibility -> {
                _state.value =
                    _state.value.copy(secondPasswordVisibility = !_state.value.secondPasswordVisibility)
            }
            is ProfileEvent.ProfileImageChanged -> {
                event.uri?.let {
                    saveImageToInternalStorage(it)
                }
            }
            is ProfileEvent.OnConfirmClick -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)

                    val request = if (_state.value.oldPassword.length >= 6 && _state.value.password.length >= 6
                        && _state.value.secondPassword == _state.value.password) {
                        UserUpdateRequest(
                            username = _state.value.username,
                            phoneNumber = _state.value.phoneNumber,
                            oldPassword = _state.value.oldPassword,
                            newPassword = _state.value.password
                        )
                    } else {
                        UserUpdateRequest(
                            username = _state.value.username,
                            phoneNumber = _state.value.phoneNumber
                        )
                    }

                    val response = authRepository.updateUser(request)
                    if (response.isSuccessful) {
                        _effect.emit(ProfileEffect.OnProfileChanged("Profile updated successfully"))
                        loadUser()
                    } else {
                        _effect.emit(ProfileEffect.OnProfileChanged("Failed to update profile"))
                    }
                    _state.value = _state.value.copy(
                        oldPassword = "",
                        password = "",
                        secondPassword = "",
                        isLoading = false
                    )
                }
            }

            ProfileEvent.OnSignOutClick -> viewModelScope.launch {
                UserPreferences.clearUserId(context)
                UserPreferences.clearToken(context)
                UserPreferences.clearProfileImagePath(context)
                _effect.emit(ProfileEffect.OnSignOutClick)
            }

            is ProfileEvent.OldPasswordChanged -> {
                _state.value = _state.value.copy(oldPassword = event.password)
            }
            is ProfileEvent.ToggleOldPasswordVisibility -> {
                _state.value =
                    _state.value.copy(oldPasswordVisibility = !_state.value.oldPasswordVisibility)
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri) = viewModelScope.launch {
            val inputStream = context.contentResolver.openInputStream(uri)
            val destinationFile = File(context.filesDir, "profile_image.jpg")

            inputStream?.use { input ->
                destinationFile.outputStream().use { input.copyTo(it) }
            }

            UserPreferences.saveProfileImagePath(context, destinationFile.toUri().toString())
            _state.value = _state.value.copy(imageUri = destinationFile.toUri())
    }

    private fun loadUser() = viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true)

        val storedUserId = UserPreferences.userIdFlow(context).first()
        val imagePath = UserPreferences.profileImagePathFlow(context).first()

        storedUserId?.let {
            val response = authRepository.getUser(it)

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                _state.value = _state.value.copy(
                    user = user,
                    username = user.username,
                    phoneNumber = user.phoneNumber,
                    imageUri = imagePath?.toUri(),
                    isLoading = false
                )
            }
        }
    }

    init {
        loadUser()
    }
}
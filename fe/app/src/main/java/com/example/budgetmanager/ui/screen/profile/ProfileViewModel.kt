package com.example.budgetmanager.ui.screen.profile

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.User
import com.example.budgetmanager.data.local.UserPreferences
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
    val user: User? = null,
    val username: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val secondPassword: String = "",
    val passwordVisibility: Boolean = false,
    val secondPasswordVisibility: Boolean = false,
    val imageUri: Uri? = null
)

sealed interface ProfileEvent {
    data class UsernameChanged(val username: String) : ProfileEvent
    data class PhoneNumberChanged(val phoneNumber: String) : ProfileEvent
    data class PasswordChanged(val password: String) : ProfileEvent
    data class SecondPasswordChanged(val password: String) : ProfileEvent
    data object TogglePasswordVisibility : ProfileEvent
    data object ToggleSecondPasswordVisibility : ProfileEvent
    data class ProfileImageChanged(val uri: Uri?) : ProfileEvent
    data object OnConfirmClick : ProfileEvent
    data object OnSignOutClick : ProfileEvent
}

sealed interface ProfileEffect {
    data object OnSignOutClick : ProfileEffect
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context
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
                // Call backend to update user
            }

            ProfileEvent.OnSignOutClick -> viewModelScope.launch {
                UserPreferences.clearUserId(context)
                UserPreferences.clearProfileImagePath(context)

                // Call backend to sign out user and delete token

                _effect.emit(ProfileEffect.OnSignOutClick)
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
        val storedUserId = UserPreferences.userIdFlow(context).first()
        val imagePath = UserPreferences.profileImagePathFlow(context).first()

        _state.value = _state.value.copy(
            user = userPreview,
            username = userPreview.username,
            phoneNumber = userPreview.phoneNumber,
            imageUri = imagePath?.toUri()
        )
    }

    init {
        loadUser()
    }
}

private val userPreview = User(
    id = 1,
    username = "Marcel Ciolacu",
    phoneNumber = "+40187429434"
)
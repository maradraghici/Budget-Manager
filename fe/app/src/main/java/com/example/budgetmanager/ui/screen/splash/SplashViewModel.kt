package com.example.budgetmanager.ui.screen.splash

import android.content.Context
import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.UserPreferences
import com.example.budgetmanager.data.remote.dto.AuthorizeRequest
import com.example.budgetmanager.data.repository.AuthRepository
import com.example.budgetmanager.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            val userId = UserPreferences.userIdFlow(context).first()
            val token = UserPreferences.tokenFlow(context).first() // Assuming you have this

            if (userId == null || token == null) {
                // No session found, go to Login
                _startDestination.value = Routes.LOGIN
                return@launch
            }

            try {
                val request = AuthorizeRequest(token = token)
                val response = authRepository.authorize(request)

                if (response.isSuccessful && response.body()?.isValid == true) {
                    _startDestination.value = Routes.MAIN_APP
                } else {
                    _startDestination.value = Routes.LOGIN
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _startDestination.value = Routes.LOGIN
            }
        }
    }
}
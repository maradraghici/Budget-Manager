package com.example.budgetmanager.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface SettingsEvent {
    data object OnProfileClick : SettingsEvent
    data object OnConnectionsClick : SettingsEvent
}

sealed interface SettingsEffect {
    data object OnProfileClick : SettingsEffect
    data object OnConnectionsClick : SettingsEffect
}

class SettingsViewModel : ViewModel() {
    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnConnectionsClick -> viewModelScope.launch {
                _effect.emit(SettingsEffect.OnConnectionsClick)
            }
            SettingsEvent.OnProfileClick -> viewModelScope.launch {
                _effect.emit(SettingsEffect.OnProfileClick)
            }
        }
    }
}
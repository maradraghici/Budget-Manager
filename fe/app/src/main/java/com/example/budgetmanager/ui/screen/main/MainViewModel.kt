package com.example.budgetmanager.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainState(
    val title: String = ""
)

sealed interface MainEvent {
    data class OnTitleChanged(val title: String) : MainEvent
    data object OnSignOutClick : MainEvent
}

sealed interface MainEffect {
    data object OnSignOutClick : MainEffect
}

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MainEffect>()
    val effect = _effect.asSharedFlow()


    fun onEvent(event: MainEvent) {
        when(event) {
            is MainEvent.OnTitleChanged -> {
                _state.value = _state.value.copy(title = event.title)
            }

            is MainEvent.OnSignOutClick -> viewModelScope.launch {
                _effect.emit(MainEffect.OnSignOutClick)
            }
        }
    }
}
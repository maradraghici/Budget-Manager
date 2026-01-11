package com.example.budgetmanager.ui.screen.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class TopBarState(
    val title: String = ""
)

sealed class TopBarEvent {
    data class OnTitleChanged(val title: String) : TopBarEvent()
}

@HiltViewModel
class TopBarViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(TopBarState())
    val state = _state.asStateFlow()

    fun onEvent(event: TopBarEvent) {
        when(event) {
            is TopBarEvent.OnTitleChanged -> {
                _state.value = _state.value.copy(title = event.title)
            }
        }
    }
}
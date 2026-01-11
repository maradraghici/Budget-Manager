package com.example.budgetmanager.ui.screen.connections

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.User
import com.example.budgetmanager.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConnectionsState(
    val connectionsList: List<User> = emptyList(),
    val showDeleteConnection: Boolean = false,
    val deleteConnectionId: Long? = null,
    val showAddConnection: Boolean = false,
    val addConnectionPhoneNumber: String = "+"
)

sealed interface ConnectionsEvent {
    data class OnConnectionClick(val id: Long) : ConnectionsEvent
    data object ShowDeleteConnectionChanged : ConnectionsEvent
    data object OnDeleteConnectionClicked : ConnectionsEvent
    data object ShowAddConnectionChanged : ConnectionsEvent
    data object OnAddConnectionClicked : ConnectionsEvent
    data class AddConnectionPhoneNumberChanged(val phoneNumber: String) : ConnectionsEvent
}

@HiltViewModel
class ConnectionsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
): ViewModel() {
    private val _state = MutableStateFlow(ConnectionsState())
    val state = _state.asStateFlow()

    fun onEvent(event: ConnectionsEvent) {
        when(event) {
            is ConnectionsEvent.ShowAddConnectionChanged -> {
                _state.value = _state.value.copy(
                    showAddConnection = !_state.value.showAddConnection,
                    addConnectionPhoneNumber = "+"
                )
            }
            is ConnectionsEvent.ShowDeleteConnectionChanged -> {
                _state.value = _state.value.copy(
                    showDeleteConnection = false,
                    deleteConnectionId = null
                )
            }
            is ConnectionsEvent.OnConnectionClick -> {
                _state.value = _state.value.copy(
                    showDeleteConnection = true,
                    deleteConnectionId = event.id
                )
            }
            is ConnectionsEvent.OnAddConnectionClicked -> {
                // Call backend to add connection
            }
            is ConnectionsEvent.OnDeleteConnectionClicked -> {
                // Call backend to delete connection
            }
            is ConnectionsEvent.AddConnectionPhoneNumberChanged -> {
                _state.value = _state.value.copy(
                    addConnectionPhoneNumber = event.phoneNumber
                )
            }
        }
    }

    private fun loadConnections()  = viewModelScope.launch {
        val storedUserId = UserPreferences.userIdFlow(context).first()
        _state.value = _state.value.copy(
            connectionsList = connectionsListLocal
        )
    }

    init {
        loadConnections()
    }
}

private val connectionsListLocal : List<User> = listOf(
    User(0, "Georgescu", "+2354235234"),
    User(1, "Nicusor", "+2354235234"),
    User(2, "Marius", "+2354235234"),
    User(3, "Vasile", "+2354235234"),
    User(4, "Iliescu", "+2354235234"),
)
package com.example.budgetmanager.ui.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.BudgetPreview
import com.example.budgetmanager.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val budgetPreviewList: List<BudgetPreview> = budgetPreviewListLocal,
    val budgetName: String = "",
    val budgetDescription: String = "",
    val deleteBudgetId: Long? = null,
    val showCreateBudget: Boolean = false,
    val showDeleteBudget: Boolean = false
)

sealed interface HomeEvent {
    data class BudgetNameChanged(val name: String) : HomeEvent
    data class BudgetDescriptionChanged(val description: String) : HomeEvent
    data object ShowCreateBudgetChanged : HomeEvent
    data object ShowDeleteBudgetChanged : HomeEvent
    data object CreateBudgetClicked : HomeEvent
    data object DeleteBudgetClicked : HomeEvent
    data class OnBudgetHold(val id: Long, val ownerId: Long) : HomeEvent

    data class OnBudgetClick(val id: Long) : HomeEvent
}

sealed interface HomeEffect {
    data class OnBudgetClick(val id: Long) : HomeEffect
}


@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ShowCreateBudgetChanged -> {
                if (_state.value.showCreateBudget) {
                    _state.value = _state.value.copy(
                        showCreateBudget = false,
                        budgetName = "",
                        budgetDescription = ""
                    )
                } else {
                    _state.value = _state.value.copy(
                        showCreateBudget = true
                    )
                }
            }
            is HomeEvent.ShowDeleteBudgetChanged -> {
                _state.value = _state.value.copy(
                    showDeleteBudget = false,
                    deleteBudgetId = null
                )
            }
            is HomeEvent.BudgetDescriptionChanged -> {
                _state.value = _state.value.copy(budgetDescription = event.description)
            }
            is HomeEvent.BudgetNameChanged -> {
                _state.value = _state.value.copy(budgetName = event.name)
            }
            is HomeEvent.CreateBudgetClicked -> {
                // Call backend to create a new budget and update the list
            }
            is HomeEvent.DeleteBudgetClicked -> {
                // Call backend to delete the budget and update the list
            }
            is HomeEvent.OnBudgetHold -> viewModelScope.launch {
                val storedUserId = UserPreferences.userIdFlow(context).first()
                if(storedUserId == event.ownerId) {
                    _state.value = _state.value.copy(
                        showDeleteBudget = true,
                        deleteBudgetId = event.id
                    )
                }
            }

            is HomeEvent.OnBudgetClick -> viewModelScope.launch {
                _effect.emit(HomeEffect.OnBudgetClick(event.id))
            }
        }
    }

}

internal val budgetPreviewListLocal : List<BudgetPreview> = listOf(
    BudgetPreview(0, "Trip to Miami", "Summer 2024", 0),
    BudgetPreview(1, "Months in Paris", "The collocation for the internship", 1),
    BudgetPreview(2, "Budget title", "Comment", 0),
)
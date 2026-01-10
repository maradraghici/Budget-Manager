package com.example.budgetmanager.ui.screen.home

import androidx.lifecycle.ViewModel
import com.example.budgetmanager.data.local.BudgetPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeState(
    val budgetPreviewList: List<BudgetPreview> = budgetPreviewListLocal,
    val budgetName: String = "",
    val budgetDescription: String = "",
    val showCreateBudget: Boolean = false
)

sealed interface HomeEvent {
    data class BudgetNameChanged(val name: String) : HomeEvent
    data class BudgetDescriptionChanged(val description: String) : HomeEvent
    data object BottomSheetStateChanged : HomeEvent
    data object CreateBudgetClicked : HomeEvent
}


class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.BottomSheetStateChanged -> {
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
            is HomeEvent.BudgetDescriptionChanged -> {
                _state.value = _state.value.copy(budgetDescription = event.description)
            }
            is HomeEvent.BudgetNameChanged -> {
                _state.value = _state.value.copy(budgetName = event.name)
            }
            is HomeEvent.CreateBudgetClicked -> {
                // Call backend to create a new budget and update the list
            }
        }
    }

}

internal val budgetPreviewListLocal : List<BudgetPreview> = listOf(
    BudgetPreview(0, "Trip to Miami", "Summer 2024"),
    BudgetPreview(1, "Months in Paris", "The collocation for the internship"),
    BudgetPreview(2, "Budget title", "Comment"),
)
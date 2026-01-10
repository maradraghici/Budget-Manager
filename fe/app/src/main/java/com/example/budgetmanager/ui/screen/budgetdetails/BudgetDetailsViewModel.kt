package com.example.budgetmanager.ui.screen.budgetdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.BudgetDetails
import com.example.budgetmanager.data.local.Expense
import com.example.budgetmanager.data.local.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BudgetDetailsState(
    val budgetDetails: BudgetDetails? = null,
)

sealed interface BudgetDetailsEvent {
}

sealed interface BudgetDetailsEffect {
}

@HiltViewModel
class BudgetDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val id: Long = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(BudgetDetailsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BudgetDetailsEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: BudgetDetailsEvent) {

    }

    private fun loadBudgetDetails() {
        _state.value = _state.value.copy(
            budgetDetails = budgetDetailsPreview
        )
    }

    init {
        loadBudgetDetails()
    }

}

internal val budgetDetailsPreview = BudgetDetails(
    title = "Trip to Miami",
    expenses = listOf(
        Expense(
            name = "Hotel",
            amount = 200.0,
            user = User(
                id = 1,
                username = "Marcel",
                phoneNumber = "+40723456789"
            ),
            date = "21/03/2025"
        ),
        Expense(
            name = "Car Rental",
            amount = 150.0,
            user = User(
                id = 1,
                username = "Marcu",
                phoneNumber = "+40721239333"
            ),
            date = "21/03/2025"
        )
    )
)
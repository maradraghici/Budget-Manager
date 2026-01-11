package com.example.budgetmanager.ui.screen.budgetdetails

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.BudgetDetails
import com.example.budgetmanager.data.local.Expense
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
import javax.inject.Inject

data class BudgetDetailsState(
    val budgetDetails: BudgetDetails? = null,
    val showOptions: Boolean = false,
    val showDeleteExpense: Boolean = false,
    val showAddExpense: Boolean = false,
    val showAddUser: Boolean = false,
    val showRemoveUser: Boolean = false,
    val deleteExpenseId: Long? = null,
    val newExpenseName: String = "",
    val newExpensePrice: String = "",
    val newUserPhoneNumber: String = "+",
    val removeUserPhoneNumber: String = "+"
)

sealed interface BudgetDetailsEvent {
    data object ShowDeleteExpenseChanged: BudgetDetailsEvent
    data object DeleteExpenseClicked: BudgetDetailsEvent
    data class OnExpenseClicked(val id: Long, val ownerId: Long): BudgetDetailsEvent
    data class NewExpenseNameChanged(val name: String) : BudgetDetailsEvent
    data class NewExpensePriceChanged(val price: String) : BudgetDetailsEvent
    data class NewUserPhoneNumberChanged(val phoneNumber: String) : BudgetDetailsEvent
    data class RemoveUserPhoneNumberChanged(val phoneNumber: String) : BudgetDetailsEvent
    data object OptionsClicked: BudgetDetailsEvent
    data object ShowAddExpenseChanged: BudgetDetailsEvent
    data object ShowAddUserChanged: BudgetDetailsEvent
    data object ShowRemoveUserChanged: BudgetDetailsEvent
    data object AddExpenseClicked : BudgetDetailsEvent
    data object AddUserClicked : BudgetDetailsEvent
    data object RemoveUserClicked : BudgetDetailsEvent
    data object OnSummaryClick : BudgetDetailsEvent
}

sealed interface BudgetDetailsEffect {
    data class OnSummaryClick(val id: Long) : BudgetDetailsEffect
}

@HiltViewModel
class BudgetDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val id: Long = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(BudgetDetailsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BudgetDetailsEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: BudgetDetailsEvent) {
        when(event) {
            is BudgetDetailsEvent.DeleteExpenseClicked -> {
                // Call backend to delete the expense and update the list
            }
            is BudgetDetailsEvent.ShowDeleteExpenseChanged -> {
                _state.value = _state.value.copy(
                    showDeleteExpense = false,
                    deleteExpenseId = null
                )
            }
            is BudgetDetailsEvent.OnExpenseClicked -> viewModelScope.launch {
                val storedUserId = UserPreferences.userIdFlow(context).first()
                if(storedUserId == event.ownerId) {
                    _state.value = _state.value.copy(
                        showDeleteExpense = true,
                        deleteExpenseId = event.id
                    )
                }
            }
            is BudgetDetailsEvent.OptionsClicked -> {
                _state.value = _state.value.copy(
                    showOptions = !_state.value.showOptions
                )
            }
            is BudgetDetailsEvent.ShowAddExpenseChanged -> {
                if (_state.value.showAddExpense) {
                    _state.value = _state.value.copy(
                        showAddExpense = false,
                        newExpenseName = "",
                        newExpensePrice = ""
                    )
                } else {
                    _state.value = _state.value.copy(
                        showAddExpense = true
                    )
                }
            }
            is BudgetDetailsEvent.ShowAddUserChanged -> {
                if (_state.value.showAddUser) {
                    _state.value = _state.value.copy(
                        showAddUser = false,
                        newUserPhoneNumber = "+"
                    )
                } else {
                    _state.value = _state.value.copy(
                        showAddUser = true
                    )
                }
            }
            is BudgetDetailsEvent.ShowRemoveUserChanged -> {
                if (_state.value.showRemoveUser) {
                    _state.value = _state.value.copy(
                        showRemoveUser = false,
                        removeUserPhoneNumber = "+"
                    )
                } else {
                    _state.value = _state.value.copy(
                        showRemoveUser = true
                    )
                }
            }
            is BudgetDetailsEvent.AddExpenseClicked -> {
                // Call backend to add the expense and update the list
            }
            is BudgetDetailsEvent.NewExpenseNameChanged -> {
                _state.value = _state.value.copy(newExpenseName = event.name)
            }
            is BudgetDetailsEvent.NewExpensePriceChanged -> {
                _state.value = _state.value.copy(newExpensePrice = event.price)
            }
            is BudgetDetailsEvent.NewUserPhoneNumberChanged -> {
                _state.value = _state.value.copy(newUserPhoneNumber = event.phoneNumber)
            }
            is BudgetDetailsEvent.AddUserClicked -> {
                // Call backend to add the user
            }
            is BudgetDetailsEvent.RemoveUserPhoneNumberChanged -> {
                _state.value = _state.value.copy(removeUserPhoneNumber = event.phoneNumber)
            }
            is BudgetDetailsEvent.RemoveUserClicked -> {
                // Call backend to remove the user
            }

            is BudgetDetailsEvent.OnSummaryClick -> viewModelScope.launch {
                _effect.emit(BudgetDetailsEffect.OnSummaryClick(id))
            }
        }
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
            id = 0,
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
            id = 1,
            name = "Car Rental",
            amount = 150.0,
            user = User(
                id = 2,
                username = "Marcu",
                phoneNumber = "+40721239333"
            ),
            date = "21/03/2025"
        )
    ),
    users = listOf(
        User(
            id = 1,
            username = "Marcel",
            phoneNumber = "+40723456789"
        ),
        User(
            id = 2,
            username = "Marcu",
            phoneNumber = "+40721239333",
        )
    )
)
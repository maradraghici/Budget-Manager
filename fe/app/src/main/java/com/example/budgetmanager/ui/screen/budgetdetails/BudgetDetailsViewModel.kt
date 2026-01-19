package com.example.budgetmanager.ui.screen.budgetdetails

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.BudgetDetails
import com.example.budgetmanager.data.local.Expense
import com.example.budgetmanager.data.local.User
import com.example.budgetmanager.data.local.UserPreferences
import com.example.budgetmanager.data.remote.dto.CreateExpenseRequest
import com.example.budgetmanager.data.repository.BudgetsRepository
import com.example.budgetmanager.data.repository.ExpensesRepository
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
    val expenses: List<Expense> = emptyList(),
    val budgetTitle: String = "",
    val showOptions: Boolean = false,
    val showDeleteExpense: Boolean = false,
    val showAddExpense: Boolean = false,
    val showAddUser: Boolean = false,
    val showRemoveUser: Boolean = false,
    val deleteExpenseId: Long? = null,
    val newExpenseName: String = "",
    val newExpensePrice: String = "",
    val newExpenseDescription: String = "",
    val newUserPhoneNumber: String = "+",
    val removeUserPhoneNumber: String = "+",
    val isLoading: Boolean = false,
    val userId: Long = -1
)

sealed interface BudgetDetailsEvent {
    data object ShowDeleteExpenseChanged: BudgetDetailsEvent
    data object DeleteExpenseClicked: BudgetDetailsEvent
    data class OnExpenseClicked(val id: Long, val ownerId: Long): BudgetDetailsEvent
    data class NewExpenseNameChanged(val name: String) : BudgetDetailsEvent
    data class NewExpensePriceChanged(val price: String) : BudgetDetailsEvent
    data class NewExpenseDescriptionChanged(val description: String) : BudgetDetailsEvent
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
    data class OnBudgetDetailsChanged(val message: String): BudgetDetailsEffect
}

@HiltViewModel
class BudgetDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val expensesRepository: ExpensesRepository,
    private val budgetRepository: BudgetsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val id: Long = checkNotNull(savedStateHandle["id"])
    val title: String = checkNotNull(savedStateHandle["title"])

    private val _state = MutableStateFlow(BudgetDetailsState(budgetTitle = title))
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BudgetDetailsEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: BudgetDetailsEvent) {
        when(event) {
            is BudgetDetailsEvent.DeleteExpenseClicked -> {
                deleteExpense()
            }
            is BudgetDetailsEvent.ShowDeleteExpenseChanged -> {
                _state.value = _state.value.copy(
                    showDeleteExpense = false,
                    deleteExpenseId = null
                )
            }
            is BudgetDetailsEvent.OnExpenseClicked -> viewModelScope.launch {
                _state.value = _state.value.copy(
                    showDeleteExpense = true,
                    deleteExpenseId = event.id
                )
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
                addExpense()
            }
            is BudgetDetailsEvent.NewExpenseNameChanged -> {
                _state.value = _state.value.copy(newExpenseName = event.name)
            }
            is BudgetDetailsEvent.NewExpensePriceChanged -> {
                _state.value = _state.value.copy(newExpensePrice = event.price)
            }
            is BudgetDetailsEvent.NewExpenseDescriptionChanged -> {
                _state.value = _state.value.copy(newExpenseDescription = event.description)
            }
            is BudgetDetailsEvent.NewUserPhoneNumberChanged -> {
                _state.value = _state.value.copy(newUserPhoneNumber = event.phoneNumber)
            }
            is BudgetDetailsEvent.AddUserClicked -> {
                addUser()
            }
            is BudgetDetailsEvent.RemoveUserPhoneNumberChanged -> {
                _state.value = _state.value.copy(removeUserPhoneNumber = event.phoneNumber)
            }
            is BudgetDetailsEvent.RemoveUserClicked -> {
                removeUser()
            }

            is BudgetDetailsEvent.OnSummaryClick -> viewModelScope.launch {
                _effect.emit(BudgetDetailsEffect.OnSummaryClick(id))
            }
        }
    }

    private fun loadBudgetDetails() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val userId = UserPreferences.userIdFlow(context).first()
            userId?.let {
                _state.value = _state.value.copy(userId = it)
            }

            val response = expensesRepository.getExpenses(id)
            if (response.isSuccessful && response.body() != null) {
                _state.value = _state.value.copy(
                    expenses = response.body()!!
                )
            }
            _state.value = _state.value.copy(
                showAddExpense = false,
                newExpenseName = "",
                newExpensePrice = "",
                isLoading = false
            )
        }
    }

    private fun addExpense() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val request = CreateExpenseRequest(
                name = _state.value.newExpenseName,
                amount = _state.value.newExpensePrice.toDouble(),
                commentary = _state.value.newExpenseDescription,
                budgetId = id,
                userId = _state.value.userId,
            )
            val response = expensesRepository.createExpense(request)
            if (response.isSuccessful) {
                _effect.emit(BudgetDetailsEffect.OnBudgetDetailsChanged("Expense added successfully"))
                loadBudgetDetails()
            } else {
                _effect.emit(BudgetDetailsEffect.OnBudgetDetailsChanged("Failed to add expense"))
            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun deleteExpense() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val response = expensesRepository.deleteExpense(_state.value.deleteExpenseId!!)
            if (response.isSuccessful) {
                _effect.emit(BudgetDetailsEffect.OnBudgetDetailsChanged("Expense deleted successfully"))
                loadBudgetDetails()
            } else {
                _effect.emit(BudgetDetailsEffect.OnBudgetDetailsChanged("Failed to delete expense"))
            }
            _state.value = _state.value.copy(
                showDeleteExpense = false,
                deleteExpenseId = null,
                isLoading = false
            )
        }
    }

    private fun addUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val response = budgetRepository.addUserToBudget(_state.value.newUserPhoneNumber, id)
            if (response.isSuccessful) {
                _effect.emit(BudgetDetailsEffect.OnBudgetDetailsChanged("User added successfully"))
            } else {
                _effect.emit(BudgetDetailsEffect.OnBudgetDetailsChanged("Failed to add user"))
            }

            _state.value = _state.value.copy(
                showAddUser = false,
                newUserPhoneNumber = "+",
                isLoading = false
            )
        }
    }

    private fun removeUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val response = budgetRepository.removeUserFromBudget(_state.value.removeUserPhoneNumber, id)
            if (response.isSuccessful) {
                _effect.emit(BudgetDetailsEffect.OnBudgetDetailsChanged("User removed successfully"))
            } else {
                _effect.emit(BudgetDetailsEffect.OnBudgetDetailsChanged("Failed to remove user"))
            }

            _state.value = _state.value.copy(
                showRemoveUser = false,
                removeUserPhoneNumber = "+",
                isLoading = false
            )
        }
    }

    init {
        loadBudgetDetails()
    }

}
package com.example.budgetmanager.ui.screen.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.Expense
import com.example.budgetmanager.data.local.User
import com.example.budgetmanager.data.repository.BudgetsRepository
import com.example.budgetmanager.data.repository.ExpensesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

data class Debt(
    val from: String,
    val to: String,
    val amount: Double
)

data class UserSpent(
    val username: String,
    val amount: Double
)

data class SummaryState(
    val expenses: List<Expense> = emptyList(),
    val users: List<User> = emptyList(),
    val total: Double = 0.0,
    val debts: List<Debt> = emptyList(),
    val totalPerUserList: List<UserSpent> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository,
    private val budgetRepository: BudgetsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val id: Long = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(SummaryState())
    val state = _state.asStateFlow()

    private fun loadBudgetDetails() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val responseExpenses = expensesRepository.getExpenses(id)
            if (responseExpenses.isSuccessful && responseExpenses.body() != null) {
                _state.value = _state.value.copy(
                    expenses = responseExpenses.body()!!
                )
            }

            val responseOwner = budgetRepository.getBudgetOwnerById(id)
            if (responseOwner.isSuccessful && responseOwner.body() != null) {
                _state.value = _state.value.copy(
                    users = listOf(responseOwner.body()!!)
                )
            }

            val responseMembers = budgetRepository.getBudgetMembersById(id)
            if (responseMembers.isSuccessful && responseMembers.body() != null) {
                _state.value = _state.value.copy(
                    users = _state.value.users + responseMembers.body()!!
                )
            }

            val totalAmount = _state.value.expenses.sumOf { it.amount }

            val totalPerUserList = _state.value.users.map { user ->
                val totalPaidByUser = _state.value.expenses
                    .filter { it.user == user }
                    .sumOf { it.amount }
                val roundedTotal = String.format("%.2f", totalPaidByUser).toDouble()
                UserSpent(username = user.username, amount = roundedTotal)
            }

            if (_state.value.users.isEmpty() || _state.value.users.size == 1) {
                _state.value = _state.value.copy(
                    total = totalAmount,
                    totalPerUserList = totalPerUserList,
                    debts = emptyList(),
                    isLoading = false
                )
                return@launch
            }

            val average = totalAmount / _state.value.users.size

            val balances = _state.value.users.associate { user ->
                val totalPaidByUser =
                    _state.value.expenses.filter { it.user == user }.sumOf { it.amount }
                user.username to (totalPaidByUser - average)
            }

            val sortedDebtors = balances.filter { it.value > 0 }.toList().sortedBy { it.second }
            val sortedCreditors =
                balances.filter { it.value < 0 }.toList().sortedByDescending { it.second }

            val calculatedDebts = mutableListOf<Debt>()

            var debtorIndex = 0
            var creditorIndex = 0
            val debtorBalances = sortedDebtors.map { it.second }.toMutableList()
            val creditorBalances = sortedCreditors.map { it.second }.toMutableList()

            while (debtorIndex < sortedDebtors.size && creditorIndex < sortedCreditors.size) {
                val debtorName = sortedDebtors[debtorIndex].first
                val creditorName = sortedCreditors[creditorIndex].first

                val amountOwed = debtorBalances[debtorIndex]
                val amountToReceive = abs(creditorBalances[creditorIndex])

                val settlementAmount = minOf(amountOwed, amountToReceive)
                val roundedAmount = String.format("%.2f", settlementAmount).toDouble()

                if (settlementAmount > 0.01) {
                    calculatedDebts.add(
                        Debt(
                            from = creditorName,
                            to = debtorName,
                            amount = roundedAmount
                        )
                    )
                }

                debtorBalances[debtorIndex] -= settlementAmount
                creditorBalances[creditorIndex] += settlementAmount

                if (debtorBalances[debtorIndex] < 0.01) {
                    debtorIndex++
                }

                if (abs(creditorBalances[creditorIndex]) < 0.01) {
                    creditorIndex++
                }
            }

            _state.value = _state.value.copy(
                total = totalAmount,
                debts = calculatedDebts,
                totalPerUserList = totalPerUserList,
                isLoading = false
            )
        }
    }

    init {
        loadBudgetDetails()
    }
}
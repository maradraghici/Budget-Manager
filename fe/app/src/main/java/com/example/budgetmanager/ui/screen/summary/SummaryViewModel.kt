package com.example.budgetmanager.ui.screen.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.budgetmanager.data.local.BudgetDetails
import com.example.budgetmanager.ui.screen.budgetdetails.budgetDetailsPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val budgetDetails: BudgetDetails? = null,
    val total: Double = 0.0,
    val debts: List<Debt> = emptyList(),
    val totalPerUserList: List<UserSpent> = emptyList()
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val id: Long = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(SummaryState())
    val state = _state.asStateFlow()

    private fun loadBudgetDetails() {
        val details = budgetDetailsPreview
        val totalAmount = details.expenses.sumOf { it.amount }

        val totalPerUserList = details.users.map { user ->
            val totalPaidByUser = details.expenses
                .filter { it.user == user }
                .sumOf { it.amount }
            UserSpent(username = user.username, amount = totalPaidByUser)
        }

        if (details.users.isEmpty() || details.users.size == 1) {
            _state.value = _state.value.copy(
                budgetDetails = details,
                total = totalAmount,
                debts = emptyList()
            )
            return
        }

        val average = totalAmount / details.users.size

        val balances = details.users.associate { user ->
            val totalPaidByUser = details.expenses.filter { it.user == user }.sumOf { it.amount }
            user.username to (totalPaidByUser - average)
        }

        val sortedDebtors = balances.filter { it.value > 0 }.toList().sortedBy { it.second }
        val sortedCreditors = balances.filter { it.value < 0 }.toList().sortedByDescending { it.second }

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

            if (settlementAmount > 0.01) {
                calculatedDebts.add(
                    Debt(
                        from = debtorName,
                        to = creditorName,
                        amount = settlementAmount
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
            budgetDetails = details,
            total = totalAmount,
            debts = calculatedDebts,
            totalPerUserList = totalPerUserList
        )
    }

    init {
        loadBudgetDetails()
    }
}
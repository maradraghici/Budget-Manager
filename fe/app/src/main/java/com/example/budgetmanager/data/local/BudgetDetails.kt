package com.example.budgetmanager.data.local

data class BudgetDetails(
    val title: String,
    val expenses: List<Expense>,
    val users: List<User>
)
package com.example.budgetmanager.data.local

data class Expense(
    val name: String,
    val amount: Double,
    val user: User,
    val date: String
)
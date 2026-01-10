package com.example.budgetmanager.data.local

data class Expense(
    val id: Long,
    val name: String,
    val amount: Double,
    val user: User,
    val date: String
)
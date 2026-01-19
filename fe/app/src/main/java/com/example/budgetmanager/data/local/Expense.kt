package com.example.budgetmanager.data.local

data class Expense(
    val id: Long,
    val name: String,
    val amount: Double,
    val description: String,
    val user: User,
    val date: String
)
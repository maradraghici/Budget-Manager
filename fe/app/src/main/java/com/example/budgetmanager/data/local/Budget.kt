package com.example.budgetmanager.data.local

data class Budget(
    val id: Long,
    val title: String,
    val description: String,
    val isOwner: Boolean
)
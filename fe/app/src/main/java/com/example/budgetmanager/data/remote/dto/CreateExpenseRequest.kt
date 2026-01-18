package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateExpenseRequest(
    @SerializedName("expendsName")
    val name: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("commentary")
    val commentary: String = "",
    @SerializedName("budgetId")
    val budgetId: Long,
    @SerializedName("paidById")
    val userId: Long
)
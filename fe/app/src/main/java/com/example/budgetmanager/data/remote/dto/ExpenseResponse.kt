package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ExpenseResponse(
    @SerializedName("expendsId")
    val id: Long,
    @SerializedName("expendsName")
    val name: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("paidBy")
    val paidBy: User,
    @SerializedName("createdAt")
    val createdAt: String
)
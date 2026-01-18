package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserBudgetResponse(
    @SerializedName("budgetId")
    val budgetId: Long,
    @SerializedName("budgetName")
    val budgetName: String,
    @SerializedName("commentary")
    val commentary: String
)
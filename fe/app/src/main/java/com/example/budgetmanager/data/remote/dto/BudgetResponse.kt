package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BudgetResponse(
    @SerializedName("budgetId")
    val id: Long,
    @SerializedName("budgetName")
    val title: String,
    @SerializedName("commentary")
    val description: String,
)
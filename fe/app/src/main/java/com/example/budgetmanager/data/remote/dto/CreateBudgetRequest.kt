package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateBudgetRequest(
    @SerializedName("budgetName")
    val budgetName: String,
    @SerializedName("commentary")
    val budgetDescription: String,
    @SerializedName("userId")
    val userId: Long
)
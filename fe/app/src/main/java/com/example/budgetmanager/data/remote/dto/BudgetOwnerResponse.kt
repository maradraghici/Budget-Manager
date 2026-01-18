package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BudgetOwnerResponse(
    @SerializedName("user")
    val user: User
)
package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BudgetMemberResponse(
    @SerializedName("budget")
    val budget: BudgetResponse
)
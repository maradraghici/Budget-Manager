package com.example.budgetmanager.data.mapper

import com.example.budgetmanager.data.local.Budget
import com.example.budgetmanager.data.remote.dto.UserBudgetResponse

fun UserBudgetResponse.toBudgetOwner(): Budget {
    return Budget(
        id = this.budgetId,
        title = this.budgetName,
        description = this.commentary,
        isOwner = true
    )
}

fun UserBudgetResponse.toBudgetMember(): Budget {
    return Budget(
        id = this.budgetId,
        title = this.budgetName,
        description = this.commentary,
        isOwner = false
    )
}
package com.example.budgetmanager.data.repository

import com.example.budgetmanager.data.remote.dto.UserBudgetResponse
import retrofit2.Response

interface BudgetsRepository {
    suspend fun getBudgets(userId: Long): Response<List<UserBudgetResponse>>
}
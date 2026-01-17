package com.example.budgetmanager.data.repository

import com.example.budgetmanager.data.local.Budget
import com.example.budgetmanager.data.remote.dto.CreateBudgetRequest
import com.example.budgetmanager.data.remote.dto.UserBudgetResponse
import retrofit2.Response

interface BudgetsRepository {
    suspend fun getBudgetsForOwner(userId: Long): Response<List<Budget>>
    suspend fun getBudgetsForMember(userId: Long): Response<List<Budget>>
    suspend fun createBudget(createBudgetRequest: CreateBudgetRequest): Response<Unit>
    suspend fun deleteBudget(budgetId: Long): Response<Unit>
}
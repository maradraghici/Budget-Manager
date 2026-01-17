package com.example.budgetmanager.data.repository

import com.example.budgetmanager.data.remote.BudgetsApiService
import com.example.budgetmanager.data.remote.dto.UserBudgetResponse
import retrofit2.Response
import javax.inject.Inject

class BudgetsRepositoryImpl @Inject constructor(
    private val budgetsApiService: BudgetsApiService
): BudgetsRepository {
    override suspend fun getBudgets(userId: Long): Response<List<UserBudgetResponse>> {
        return budgetsApiService.getBudgetsByUserId(userId)
    }
}
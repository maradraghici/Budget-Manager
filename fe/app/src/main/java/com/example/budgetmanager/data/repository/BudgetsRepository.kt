package com.example.budgetmanager.data.repository

import com.example.budgetmanager.data.local.Budget
import com.example.budgetmanager.data.local.User
import com.example.budgetmanager.data.remote.dto.CreateBudgetRequest
import com.example.budgetmanager.data.remote.dto.UserBudgetResponse
import retrofit2.Response

interface BudgetsRepository {
    suspend fun getBudgetsForOwner(userId: Long): Response<List<Budget>>
    suspend fun getBudgetsForMember(userId: Long): Response<List<Budget>>
    suspend fun getBudgetOwnerById(budgetId: Long): Response<User>
    suspend fun getBudgetMembersById(budgetId: Long): Response<List<User>>
    suspend fun addUserToBudget(phoneNumber: String, budgetId: Long): Response<Unit>
    suspend fun removeUserFromBudget(phoneNumber: String, budgetId: Long): Response<Unit>
    suspend fun createBudget(createBudgetRequest: CreateBudgetRequest): Response<Unit>
    suspend fun deleteBudget(budgetId: Long): Response<Unit>
}
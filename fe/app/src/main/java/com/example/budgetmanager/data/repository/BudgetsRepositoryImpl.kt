package com.example.budgetmanager.data.repository

import com.example.budgetmanager.data.local.Budget
import com.example.budgetmanager.data.local.User
import com.example.budgetmanager.data.mapper.toBudgetMember
import com.example.budgetmanager.data.mapper.toBudgetOwner
import com.example.budgetmanager.data.mapper.toUser
import com.example.budgetmanager.data.remote.BudgetsApiService
import com.example.budgetmanager.data.remote.dto.AddUserRequest
import com.example.budgetmanager.data.remote.dto.CreateBudgetRequest
import retrofit2.Response
import javax.inject.Inject

class BudgetsRepositoryImpl @Inject constructor(
    private val budgetsApiService: BudgetsApiService
): BudgetsRepository {
    override suspend fun getBudgetsForOwner(userId: Long): Response<List<Budget>> {
        val response = budgetsApiService.getBudgetsByOwner(userId)
        if (response.isSuccessful && response.body() != null) {
            val budgets = response.body()!!
            return Response.success(budgets.map { it.toBudgetOwner() })
        } else {
            return Response.success(emptyList())
        }

    }

    override suspend fun getBudgetsForMember(userId: Long): Response<List<Budget>> {
        val response = budgetsApiService.getBudgetsByMember(userId)
        if (response.isSuccessful && response.body() != null) {
            val budgets = response.body()!!
            return Response.success(budgets.map { it.toBudgetMember() })
        } else {
            return Response.success(emptyList())
        }
    }

    override suspend fun getBudgetOwnerById(budgetId: Long): Response<User> {
        val response = budgetsApiService.getBudgetOwnerById(budgetId)
        return if (response.isSuccessful && response.body() != null) {
            Response.success(response.body()!!.user.toUser())
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }

    override suspend fun getBudgetMembersById(budgetId: Long): Response<List<User>> {
        val response = budgetsApiService.getBudgetMembersById(budgetId)
        if (response.isSuccessful && response.body() != null) {
            val budgets = response.body()!!
            return Response.success(budgets.map { it.user.toUser() })
        } else {
            return Response.success(emptyList())
        }
    }

    override suspend fun addUserToBudget(phoneNumber: String, budgetId: Long): Response<Unit> {
        val request = AddUserRequest(phoneNumber, budgetId)
        return budgetsApiService.addUserToBudget(request)
    }

    override suspend fun removeUserFromBudget(phoneNumber: String, budgetId: Long): Response<Unit> {
        val request = AddUserRequest(phoneNumber, budgetId)
        return budgetsApiService.removeUserFromBudget(request)
    }

    override suspend fun createBudget(createBudgetRequest: CreateBudgetRequest): Response<Unit> {
        return budgetsApiService.createBudget(createBudgetRequest)
    }

    override suspend fun deleteBudget(budgetId: Long): Response<Unit> {
        return budgetsApiService.deleteBudget(budgetId)
    }
}
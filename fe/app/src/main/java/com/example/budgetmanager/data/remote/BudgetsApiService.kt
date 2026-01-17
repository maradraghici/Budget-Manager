package com.example.budgetmanager.data.remote

import com.example.budgetmanager.data.remote.dto.CreateBudgetRequest
import com.example.budgetmanager.data.remote.dto.UserBudgetResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BudgetsApiService {
    @GET("/budget/creat/{userId}")
    suspend fun getBudgetsByOwner(@Path("userId") userId: Long): Response<List<UserBudgetResponse>>

    @GET("/userBudget/user/{userId}")
    suspend fun getBudgetsByMember(@Path("userId") userId: Long): Response<List<UserBudgetResponse>>

    @POST("/budget/create")
    suspend fun createBudget(@Body request: CreateBudgetRequest): Response<Unit>

    @DELETE("/budget/delete/{budgetId}")
    suspend fun deleteBudget(@Path("budgetId") budgetId: Long): Response<Unit>
}
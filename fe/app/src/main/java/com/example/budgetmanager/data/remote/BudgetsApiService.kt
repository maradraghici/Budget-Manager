package com.example.budgetmanager.data.remote

import com.example.budgetmanager.data.remote.dto.UserBudgetResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BudgetsApiService {
    @GET("/userbudget/user/{userId}")
    suspend fun getBudgetsByUserId(@Path("userId") userId: Long): Response<List<UserBudgetResponse>>
}
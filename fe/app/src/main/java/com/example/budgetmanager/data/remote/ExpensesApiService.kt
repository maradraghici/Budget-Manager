package com.example.budgetmanager.data.remote

import com.example.budgetmanager.data.remote.dto.CreateExpenseRequest
import com.example.budgetmanager.data.remote.dto.ExpenseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ExpensesApiService {
    @POST("/expends/create")
    suspend fun createExpense(@Body request: CreateExpenseRequest): Response<Unit>

    @GET("/expends/budget/{budgetId}")
    suspend fun getExpenses(@Path("budgetId") budgetId: Long): Response<List<ExpenseResponse>>

    @DELETE("/expends/delete/{expendsId}")
    suspend fun deleteExpense(@Path("expendsId") expendsId: Long): Response<Unit>
}
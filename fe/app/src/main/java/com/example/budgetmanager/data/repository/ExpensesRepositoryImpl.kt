package com.example.budgetmanager.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.budgetmanager.data.local.Expense
import com.example.budgetmanager.data.mapper.toExpense
import com.example.budgetmanager.data.remote.ExpensesApiService
import com.example.budgetmanager.data.remote.dto.CreateExpenseRequest
import retrofit2.Response
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(
    private val expensesApiService: ExpensesApiService
): ExpensesRepository {
    override suspend fun createExpense(expense: CreateExpenseRequest): Response<Unit> {
        return expensesApiService.createExpense(expense)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getExpenses(budgetId: Long): Response<List<Expense>> {
        val response =  expensesApiService.getExpenses(budgetId)
        if (response.isSuccessful && response.body() != null) {
            val expensesRemote = response.body()!!
            return Response.success(expensesRemote.map { it.toExpense() })
        } else {
            return Response.success(emptyList())
        }
    }

    override suspend fun deleteExpense(expendsId: Long): Response<Unit> {
        return expensesApiService.deleteExpense(expendsId)
    }
}
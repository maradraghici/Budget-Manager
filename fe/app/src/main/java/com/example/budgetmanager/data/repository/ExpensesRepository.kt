package com.example.budgetmanager.data.repository

import com.example.budgetmanager.data.local.Expense
import com.example.budgetmanager.data.remote.dto.CreateExpenseRequest
import retrofit2.Response

interface ExpensesRepository {
    suspend fun createExpense(expense: CreateExpenseRequest): Response<Unit>
    suspend fun getExpenses(budgetId: Long): Response<List<Expense>>
    suspend fun deleteExpense(expendsId: Long): Response<Unit>
}
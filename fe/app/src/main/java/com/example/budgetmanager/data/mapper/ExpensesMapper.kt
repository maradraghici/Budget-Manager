package com.example.budgetmanager.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.budgetmanager.data.local.Expense
import com.example.budgetmanager.data.local.User
import com.example.budgetmanager.data.remote.dto.ExpenseResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.budgetmanager.data.remote.dto.User as UserRemote

fun UserRemote.toUser(): User {
    return User(
        id = userId,
        username = username,
        phoneNumber = phoneNumber
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun ExpenseResponse.toExpense(): Expense {
    val parsedDate = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return Expense(
        id = id,
        name = name,
        amount = amount,
        description = description,
        user = paidBy.toUser(),
        date = parsedDate.format(formatter)
    )
}
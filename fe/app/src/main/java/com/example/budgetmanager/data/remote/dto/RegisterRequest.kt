package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("username")
    val phoneNumber: String,
    @SerializedName("password")
    val password: String,
)
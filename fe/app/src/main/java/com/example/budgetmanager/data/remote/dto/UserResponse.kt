package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("username")
    val username: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String
)
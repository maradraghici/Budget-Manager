package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("userName")
    val username: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String
)
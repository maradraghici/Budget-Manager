package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthorizeRequest(
    @SerializedName("token")
    val token: String
)
package com.example.budgetmanager.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserUpdateRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("oldPassword")
    val oldPassword: String? = null,
    @SerializedName("newPassword")
    val newPassword: String? = null
)
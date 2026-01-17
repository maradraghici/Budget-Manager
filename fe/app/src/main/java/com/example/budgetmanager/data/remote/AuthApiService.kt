package com.example.budgetmanager.data.remote

import com.example.budgetmanager.data.remote.dto.RegisterRequest
import com.example.budgetmanager.data.remote.dto.AuthResponse
import com.example.budgetmanager.data.remote.dto.AuthorizeRequest
import com.example.budgetmanager.data.remote.dto.AuthorizeResponse
import com.example.budgetmanager.data.remote.dto.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("user/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("user/authenticate")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @POST("user/authorize")
    suspend fun authorize(@Body request: AuthorizeRequest): Response<AuthorizeResponse>
}
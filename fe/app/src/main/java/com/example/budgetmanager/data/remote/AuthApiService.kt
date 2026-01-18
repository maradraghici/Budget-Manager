package com.example.budgetmanager.data.remote

import com.example.budgetmanager.data.remote.dto.RegisterRequest
import com.example.budgetmanager.data.remote.dto.AuthResponse
import com.example.budgetmanager.data.remote.dto.AuthorizeRequest
import com.example.budgetmanager.data.remote.dto.AuthorizeResponse
import com.example.budgetmanager.data.remote.dto.LoginRequest
import com.example.budgetmanager.data.remote.dto.UserResponse
import com.example.budgetmanager.data.remote.dto.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {
    @POST("user/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("user/authenticate")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @POST("user/authorize")
    suspend fun authorize(@Body request: AuthorizeRequest): Response<AuthorizeResponse>

    @GET("/user/{userId}")
    suspend fun getUser(@Path("userId") userId: Long): Response<UserResponse>

    @POST("user/update")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body user: UserUpdateRequest
    ): Response<Unit>
}
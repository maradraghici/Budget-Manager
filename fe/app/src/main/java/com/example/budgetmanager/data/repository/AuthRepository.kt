package com.example.budgetmanager.data.repository

import com.example.budgetmanager.data.remote.dto.RegisterRequest
import com.example.budgetmanager.data.remote.dto.AuthResponse
import com.example.budgetmanager.data.remote.dto.AuthorizeRequest
import com.example.budgetmanager.data.remote.dto.AuthorizeResponse
import com.example.budgetmanager.data.remote.dto.LoginRequest
import com.example.budgetmanager.data.remote.dto.UserResponse
import com.example.budgetmanager.data.remote.dto.UserUpdateRequest
import retrofit2.Response

interface AuthRepository {
    suspend fun registerUser(request: RegisterRequest): Response<AuthResponse>
    suspend fun loginUser(request: LoginRequest): Response<AuthResponse>
    suspend fun authorize(request: AuthorizeRequest): Response<AuthorizeResponse>
    suspend fun getUser(userId: Long): Response<UserResponse>
    suspend fun updateUser(user: UserUpdateRequest): Response<Unit>
}
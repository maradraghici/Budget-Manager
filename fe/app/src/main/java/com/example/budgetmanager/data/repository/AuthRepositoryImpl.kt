package com.example.budgetmanager.data.repository

import com.example.budgetmanager.data.remote.AuthApiService
import com.example.budgetmanager.data.remote.dto.RegisterRequest
import com.example.budgetmanager.data.remote.dto.AuthResponse
import com.example.budgetmanager.data.remote.dto.AuthorizeRequest
import com.example.budgetmanager.data.remote.dto.AuthorizeResponse
import com.example.budgetmanager.data.remote.dto.LoginRequest
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService
): AuthRepository {
    override suspend fun registerUser(request: RegisterRequest): Response<AuthResponse> {
        return authApiService.registerUser(request)
    }

    override suspend fun loginUser(request: LoginRequest): Response<AuthResponse> {
        return authApiService.loginUser(request)
    }

    override suspend fun authorize(request: AuthorizeRequest): Response<AuthorizeResponse> {
        return authApiService.authorize(request)
    }
}
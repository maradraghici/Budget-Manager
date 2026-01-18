package com.example.budgetmanager.data.repository

import android.content.Context
import com.example.budgetmanager.data.local.UserPreferences
import com.example.budgetmanager.data.remote.AuthApiService
import com.example.budgetmanager.data.remote.dto.RegisterRequest
import com.example.budgetmanager.data.remote.dto.AuthResponse
import com.example.budgetmanager.data.remote.dto.AuthorizeRequest
import com.example.budgetmanager.data.remote.dto.AuthorizeResponse
import com.example.budgetmanager.data.remote.dto.LoginRequest
import com.example.budgetmanager.data.remote.dto.UserResponse
import com.example.budgetmanager.data.remote.dto.UserUpdateRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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

    override suspend fun getUser(userId: Long): Response<UserResponse> {
        return authApiService.getUser(userId)
    }

    override suspend fun updateUser(user: UserUpdateRequest): Response<Unit> {
        val token = UserPreferences.tokenFlow(context).first()

        return authApiService.updateUser("Bearer $token", user)
    }
}
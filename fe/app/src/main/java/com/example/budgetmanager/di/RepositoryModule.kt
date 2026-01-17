package com.example.budgetmanager.di

import com.example.budgetmanager.data.repository.AuthRepository
import com.example.budgetmanager.data.repository.AuthRepositoryImpl
import com.example.budgetmanager.data.repository.BudgetsRepository
import com.example.budgetmanager.data.repository.BudgetsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindBudgetsRepository(
        budgetsRepositoryImpl: BudgetsRepositoryImpl
    ): BudgetsRepository

}
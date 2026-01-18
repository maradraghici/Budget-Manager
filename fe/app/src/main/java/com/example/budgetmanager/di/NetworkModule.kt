package com.example.budgetmanager.di

import com.example.budgetmanager.data.remote.AuthApiService
import com.example.budgetmanager.data.remote.BudgetsApiService
import com.example.budgetmanager.data.remote.ExpensesApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // TODO: Change for the actual base URL of the backend when you run it on a different device
    private const val BASE_URL = "http://192.168.1.152:8210/"

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // See full request and response logs
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideBudgetsApiService(retrofit: Retrofit): BudgetsApiService {
        return retrofit.create(BudgetsApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideExpensesApiService(retrofit: Retrofit): ExpensesApiService {
        return retrofit.create(ExpensesApiService::class.java)
    }
}
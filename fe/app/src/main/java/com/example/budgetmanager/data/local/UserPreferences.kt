package com.example.budgetmanager.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_preferences")

object UserPreferences {
    private object PreferenceKeys {
        val USER_ID = longPreferencesKey("user_id")
    }

    fun userIdFlow(context: Context): Flow<Long?> =
        context.dataStore.data.map { preferences -> preferences[PreferenceKeys.USER_ID] }

    suspend fun saveUserId(context: Context, userId: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_ID] = userId
        }
    }

    suspend fun clearUserId(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(PreferenceKeys.USER_ID)
        }
    }
}
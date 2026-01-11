package com.example.budgetmanager.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_preferences")

object UserPreferences {
    private object PreferenceKeys {
        val USER_ID = longPreferencesKey("user_id")
        val PROFILE_IMAGE_PATH_KEY = stringPreferencesKey("profile_image_path")
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

    fun profileImagePathFlow(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.PROFILE_IMAGE_PATH_KEY]
        }
    }

    suspend fun saveProfileImagePath(context: Context, path: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.PROFILE_IMAGE_PATH_KEY] = path
        }
    }

    suspend fun clearProfileImagePath(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(PreferenceKeys.PROFILE_IMAGE_PATH_KEY)
        }
    }
}
package com.example.travelplaner.core.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val PREFERENCE_LOGGED_USER = stringPreferencesKey("logged_user_key")
private val PREFERENCE_EDIT_MODE = booleanPreferencesKey("edit_mode_key")

class AppDataRepositoryImpl(
    private val dispatchers: AppCoroutineDispatchers,
    private val context: Context
) : AppDataRepository {
    override suspend fun saveLoggedUser(username: String): Unit = withContext(dispatchers.io) {
        context.dataStore.edit { preference ->
            preference[PREFERENCE_LOGGED_USER] = username
        }
    }

    override suspend fun removeUser(username: String): Unit = withContext(dispatchers.io) {
        context.dataStore.edit { preference ->
            preference.remove(PREFERENCE_EDIT_MODE)
        }
    }

    override suspend fun getLoggedUser(): String? = withContext(dispatchers.io) {
        context.dataStore.data
            .map { preference ->
                preference[PREFERENCE_LOGGED_USER]
            }
            .first()
    }

    override suspend fun toggleEditModePreference(): Unit = withContext(dispatchers.io) {
        context.dataStore.edit { preference ->
            preference[PREFERENCE_EDIT_MODE] = !(preference[PREFERENCE_EDIT_MODE] ?: false)
        }
    }

    override suspend fun getEditMode(): Flow<Boolean> = withContext(dispatchers.io) {
        context.dataStore.data
            .map { preference ->
                preference[PREFERENCE_EDIT_MODE] ?: false
            }
    }
}
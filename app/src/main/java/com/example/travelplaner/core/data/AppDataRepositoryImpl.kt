package com.example.travelplaner.core.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.travelplaner.core.data.db.User
import com.example.travelplaner.core.data.db.dao.UserDao
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val PREFERENCE_LOGGED_USER = stringPreferencesKey("logged_user_key")
val PREFERENCE_EDIT_MODE = booleanPreferencesKey("edit_mode_key")
val PREFERENCE_APP_THEME = stringPreferencesKey("app_theme_key")

class AppDataRepositoryImpl(
    private val dispatchers: AppCoroutineDispatchers,
    private val context: Context,
    private val userDao: UserDao
) : AppDataRepository {
    override suspend fun saveLoggedUser(user: User): Unit = withContext(dispatchers.io) {
        context.dataStore.edit { preference ->
            preference[PREFERENCE_LOGGED_USER] = Json.encodeToString(user)
        }
    }

    override suspend fun removeLoggedUser() {
        context.dataStore.edit { preference ->
            preference.remove(PREFERENCE_LOGGED_USER)
        }
    }

    override suspend fun removeUser(username: String): Unit = withContext(dispatchers.io) {
        context.dataStore.edit { preference ->
            preference.remove(PREFERENCE_EDIT_MODE)
        }
    }

    override suspend fun getLoggedUser(): User? = withContext(dispatchers.io) {
        context.dataStore.data
            .map { preference ->
                preference[PREFERENCE_LOGGED_USER]
            }
            .map {
                it?.let {
                    Json.decodeFromString<User>(it)
                }
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

    override suspend fun saveAppTheme(theme: AppTheme): Unit = withContext(dispatchers.io) {
        context.dataStore.edit { preference ->
            preference[PREFERENCE_APP_THEME] = theme.name
        }
    }

    override suspend fun getAppTheme(): Flow<AppTheme> = withContext(dispatchers.io) {
        context.dataStore.data
            .map { preference ->
                when (preference[PREFERENCE_APP_THEME]) {
                    AppTheme.GREEN.name -> AppTheme.GREEN
                    AppTheme.PURPLE.name -> AppTheme.PURPLE
                    AppTheme.ORANGE.name -> AppTheme.ORANGE
                    AppTheme.BLUE.name -> AppTheme.BLUE
                    else -> AppTheme.GREEN
                }
            }
    }

    override suspend fun saveNewUser(username: String, password: String) {
        userDao.insert(
            User(
                username = username,
                password = password
            )
        )
    }
}

enum class AppTheme {
    GREEN,
    PURPLE,
    ORANGE,
    BLUE;
}
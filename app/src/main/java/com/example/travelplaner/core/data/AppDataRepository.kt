package com.example.travelplaner.core.data

import com.example.travelplaner.core.data.db.User
import kotlinx.coroutines.flow.Flow

interface AppDataRepository {
    suspend fun saveNewUser(username: String, password: String)

    suspend fun saveLoggedUser(user: User)

    suspend fun removeLoggedUser()

    suspend fun removeUser(username: String)

    suspend fun getLoggedUser() : User?

    suspend fun toggleEditModePreference()

    suspend fun getEditMode() : Flow<Boolean>

    suspend fun saveAppTheme(theme: AppTheme)

    suspend fun getAppTheme(): Flow<AppTheme>
}
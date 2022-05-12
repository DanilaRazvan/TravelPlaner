package com.example.travelplaner.core.data

import kotlinx.coroutines.flow.Flow

interface AppDataRepository {
    suspend fun saveLoggedUser(username: String)

    suspend fun removeUser(username: String)

    suspend fun getLoggedUser() : String?

    suspend fun toggleEditModePreference()

    suspend fun getEditMode() : Flow<Boolean>
}
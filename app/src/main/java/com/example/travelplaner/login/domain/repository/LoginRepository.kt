package com.example.travelplaner.login.domain.repository

import com.example.travelplaner.login.domain.model.LoginResponse
import com.example.travelplaner.core.data.TpResult
import com.example.travelplaner.login.domain.model.Credentials

interface LoginRepository {

    suspend fun login(
        credentials: Credentials
    ) : TpResult<LoginResponse>
}
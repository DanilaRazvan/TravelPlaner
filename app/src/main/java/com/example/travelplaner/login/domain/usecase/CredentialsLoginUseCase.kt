package com.example.travelplaner.login.domain.usecase

import com.example.travelplaner.login.domain.model.Credentials
import com.example.travelplaner.login.domain.model.LoginResult

interface CredentialsLoginUseCase {
    suspend operator fun invoke(
        credentials: Credentials
    ): LoginResult
}
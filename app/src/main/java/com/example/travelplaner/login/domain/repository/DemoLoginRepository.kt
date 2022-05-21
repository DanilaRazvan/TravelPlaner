package com.example.travelplaner.login.domain.repository

import com.example.travelplaner.login.domain.model.LoginResponse
import com.example.travelplaner.core.data.TpResult
import com.example.travelplaner.login.domain.model.Credentials
import com.example.travelplaner.login.domain.model.LoginException
import javax.inject.Inject

class DemoLoginRepository @Inject constructor() : LoginRepository {

    override suspend fun login(credentials: Credentials): TpResult<LoginResponse> {

        val defaultResponse = LoginResponse("token")

        if (credentials.username.value != "madalina" || credentials.password.value != "password")
            return TpResult.Failure(LoginException.InvalidCredentialsException)

        return TpResult.Success(defaultResponse)
    }
}
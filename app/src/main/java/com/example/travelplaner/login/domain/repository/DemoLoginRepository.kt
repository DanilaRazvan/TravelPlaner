package com.example.travelplaner.login.domain.repository

import com.example.travelplaner.login.domain.model.LoginResponse
import com.example.travelplaner.core.data.TpResult
import com.example.travelplaner.login.domain.model.Credentials
import javax.inject.Inject

class DemoLoginRepository @Inject constructor() : LoginRepository {

    override suspend fun login(credentials: Credentials): TpResult<LoginResponse> {

        val defaultResponse = LoginResponse("token")

        return TpResult.Success(defaultResponse)
    }
}
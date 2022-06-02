package com.example.travelplaner.login.domain.repository

import com.example.travelplaner.login.domain.model.LoginResponse
import com.example.travelplaner.core.data.TpResult
import com.example.travelplaner.core.data.db.dao.UserDao
import com.example.travelplaner.login.domain.model.Credentials
import com.example.travelplaner.login.domain.model.LoginException
import javax.inject.Inject

class DemoLoginRepository @Inject constructor(
    private val userDao: UserDao
) : LoginRepository {

    override suspend fun login(credentials: Credentials): TpResult<LoginResponse> {

        val dbUser = userDao.readByUsername(credentials.username.value) ?: return TpResult.Failure(LoginException.UserNotFound)

        if (dbUser.password != credentials.password.value)
            return TpResult.Failure(LoginException.InvalidCredentialsException)

        return TpResult.Success(LoginResponse("token", dbUser))
    }
}
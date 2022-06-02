package com.example.travelplaner.login.domain.usecase

import com.example.travelplaner.core.data.AppDataRepository
import com.example.travelplaner.core.data.GeneralErrorResult
import com.example.travelplaner.core.data.GeneralException
import com.example.travelplaner.core.data.TpResult
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.login.domain.model.Credentials
import com.example.travelplaner.login.domain.model.LoginException
import com.example.travelplaner.login.domain.model.LoginResult
import com.example.travelplaner.login.domain.repository.LoginRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CredentialsLoginUseCaseImpl @Inject constructor(
    private val loginRepository: LoginRepository,
    private val appDataRepository: AppDataRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : CredentialsLoginUseCase {

    override suspend fun invoke(credentials: Credentials): LoginResult {
        validateCredentials(credentials)?.let { validationResult ->
            return validationResult
        }

        return withContext(dispatchers.io) {
            when (val response = loginRepository.login(credentials)) {
                is TpResult.Success -> {
                    // store logged user
                    appDataRepository.saveLoggedUser(response.data.user)
                    LoginResult.Success
                }
                is TpResult.Failure -> {
                    loginResultForError(response)
                }
            }
        }
    }

    private suspend fun validateCredentials(
        credentials: Credentials
    ): LoginResult.Failure? = withContext(dispatchers.computation) {
        val emptyUsername = credentials.username.value.isEmpty()
        val emptyPassword = credentials.password.value.isEmpty()

        return@withContext if (emptyUsername || emptyPassword) {
            LoginResult.Failure.EmptyCredentials(
                emptyUsername = emptyUsername,
                emptyPassword = emptyPassword
            )
        } else null
    }


    private suspend fun loginResultForError(
        response: TpResult.Failure
    ): LoginResult.Failure = withContext(dispatchers.computation) {
        when (response.error) {
            is LoginException -> {
                when (response.error) {
                    LoginException.InvalidCredentialsException -> LoginResult.Failure.InvalidCredentials
                    LoginException.UserNotFound -> LoginResult.Failure.UserNotFound
                }
            }

            is GeneralException -> {
                when (response.error) {
                    GeneralException.NoInternetException -> LoginResult.Failure.General(
                        GeneralErrorResult.NoInternetError
                    )
                    GeneralException.SocketTimeoutException -> LoginResult.Failure.General(
                        GeneralErrorResult.TimeoutError
                    )
                }
            }

            else -> {
                LoginResult.Failure.Unknown
            }
        }
    }
}
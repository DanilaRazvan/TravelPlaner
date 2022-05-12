package com.example.travelplaner.login.domain.model

import com.example.travelplaner.core.data.GeneralErrorResult

sealed class LoginResult {
    object Success : LoginResult()

    sealed class Failure : LoginResult() {
        object InvalidCredentials : Failure()

        data class EmptyCredentials(
            val emptyUsername: Boolean,
            val emptyPassword: Boolean
        ) : Failure()

        object Unknown : Failure()

        data class General(val error: GeneralErrorResult) : Failure()
    }
}
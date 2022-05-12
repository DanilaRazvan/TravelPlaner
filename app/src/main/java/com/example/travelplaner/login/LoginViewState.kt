package com.example.travelplaner.login

import com.example.travelplaner.core.ui.UiText
import com.example.travelplaner.login.domain.model.Credentials

sealed class LoginViewState(
    open val credentials: Credentials,
    open val inputsEnabled: Boolean = true
) {
    object Initial : LoginViewState(
        credentials = Credentials()
    )

    data class Active(
        override val credentials: Credentials,
        val usernameError: UiText? = null,
        val passwordError: UiText? = null,
    ) : LoginViewState(
        credentials = credentials
    )

    data class Submitting(
        override val credentials: Credentials
    ) : LoginViewState(
        credentials = credentials,
        inputsEnabled = false
    )

    data class SubmissionError(
        override val credentials: Credentials,
        val errorMessage: UiText
    ) : LoginViewState(
        credentials = credentials
    )

    object Completed : LoginViewState(
        credentials = Credentials(),
        inputsEnabled = false
    )
}
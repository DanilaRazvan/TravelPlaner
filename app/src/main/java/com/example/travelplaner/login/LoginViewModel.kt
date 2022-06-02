package com.example.travelplaner.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.R
import com.example.travelplaner.core.data.AppDataRepository
import com.example.travelplaner.core.data.GeneralErrorResult
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.core.ui.UiText
import com.example.travelplaner.login.domain.model.Credentials
import com.example.travelplaner.login.domain.model.LoginResult
import com.example.travelplaner.login.domain.model.Password
import com.example.travelplaner.login.domain.model.Username
import com.example.travelplaner.login.domain.usecase.CredentialsLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val credentialsLoginUseCase: CredentialsLoginUseCase,
    private val appDataRepository: AppDataRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<LoginViewState>(LoginViewState.Initial)
    val viewState = _viewState.asStateFlow()

    val onUsernameChanged: (String) -> Unit = { username ->
        viewModelScope.launch(dispatchers.computation) {
            val currentCredentials = _viewState.value.credentials
            val currentUsernameError =
                (_viewState.value as? LoginViewState.Active)?.usernameError
            val currentPasswordError =
                (_viewState.value as? LoginViewState.Active)?.passwordError

            _viewState.value = LoginViewState.Active(
                credentials = currentCredentials.withUpdatedUsername(username),
                usernameError = currentUsernameError,
                passwordError = currentPasswordError
            )
        }
    }
    val onPasswordChanged: (String) -> Unit = { password ->
        viewModelScope.launch(dispatchers.computation) {
            val currentCredentials = _viewState.value.credentials
            val currentUsernameError =
                (_viewState.value as? LoginViewState.Active)?.usernameError
            val currentPasswordError =
                (_viewState.value as? LoginViewState.Active)?.passwordError

            _viewState.value = LoginViewState.Active(
                credentials = currentCredentials.withUpdatedPassword(password),
                usernameError = currentUsernameError,
                passwordError = currentPasswordError
            )
        }
    }

    val onLoginClicked: () -> Unit = {
        viewModelScope.launch(dispatchers.io) {
            val currentCredentials = _viewState.value.credentials

            _viewState.update {
                return@update LoginViewState.Submitting(currentCredentials)
            }

            val loginResult = credentialsLoginUseCase(currentCredentials)
            handleLoginResult(loginResult, currentCredentials)
        }
    }

    val onSignUp: (String, String) -> Unit = { username, password ->
        viewModelScope.launch(dispatchers.io) {
            appDataRepository.saveNewUser(username, password)
        }
    }

    private fun handleLoginResult(
        loginResult: LoginResult,
        currentCredentials: Credentials
    ) {
        _viewState.value = when (loginResult) {
            LoginResult.Success -> {
                LoginViewState.Completed
            }
            is LoginResult.Failure -> {
                when (loginResult) {
                    is LoginResult.Failure.EmptyCredentials -> {
                        loginResult.toLoginViewState(currentCredentials)
                    }
                    LoginResult.Failure.InvalidCredentials -> {
                        LoginViewState.SubmissionError(
                            credentials = currentCredentials,
                            errorMessage = UiText.StringResource(R.string.login_invalid_credentials_message)
                        )
                    }
                    LoginResult.Failure.UserNotFound -> {
                        LoginViewState.SubmissionError(
                            credentials = currentCredentials,
                            errorMessage = UiText.StringResource(R.string.login_user_not_found_message)
                        )
                    }
                    is LoginResult.Failure.General -> {
                        when (loginResult.error) {
                            GeneralErrorResult.NoInternetError -> {
                                LoginViewState.SubmissionError(
                                    credentials = currentCredentials,
                                    errorMessage = UiText.StringResource(R.string.no_internet_connection_error_message)
                                )
                            }
                            GeneralErrorResult.TimeoutError -> {
                                LoginViewState.SubmissionError(
                                    credentials = currentCredentials,
                                    errorMessage = UiText.StringResource(R.string.timeout_error_message)
                                )
                            }
                        }
                    }
                    LoginResult.Failure.Unknown -> {
                        LoginViewState.SubmissionError(
                            credentials = currentCredentials,
                            errorMessage = UiText.StringResource(R.string.login_unknown_error)
                        )
                    }
                }
            }
        }
    }

}

private fun Credentials.withUpdatedUsername(username: String): Credentials {
    return this.copy(username = Username(username))
}

private fun Credentials.withUpdatedPassword(password: String): Credentials {
    return this.copy(password = Password(password))
}

private fun LoginResult.Failure.EmptyCredentials.toLoginViewState(credentials: Credentials): LoginViewState {
    return LoginViewState.Active(
        credentials = credentials,
        usernameError = UiText.StringResource(R.string.login_empty_username_message)
            .takeIf { this.emptyUsername },
        passwordError = UiText.StringResource(R.string.login_empty_password_message)
            .takeIf { this.emptyPassword }
    )
}
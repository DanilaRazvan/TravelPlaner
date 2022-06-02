package com.example.travelplaner.login.domain.model

sealed class LoginException : Throwable() {
    object UserNotFound : LoginException()
    object InvalidCredentialsException : LoginException()
}
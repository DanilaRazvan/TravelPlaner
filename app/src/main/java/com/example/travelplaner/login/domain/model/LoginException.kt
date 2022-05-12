package com.example.travelplaner.login.domain.model

sealed class LoginException : Throwable() {
    object InvalidCredentialsException : LoginException()
}
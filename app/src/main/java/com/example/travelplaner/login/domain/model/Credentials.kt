package com.example.travelplaner.login.domain.model

@JvmInline
value class Username(val value: String)

@JvmInline
value class Password(val value: String)

data class Credentials(
    val username: Username = Username(""),
    val password: Password = Password(""),
)
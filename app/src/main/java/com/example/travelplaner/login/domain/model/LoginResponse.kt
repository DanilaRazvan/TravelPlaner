package com.example.travelplaner.login.domain.model

import com.example.travelplaner.core.data.db.User

data class LoginResponse(
    val token: String,
    val user: User
)
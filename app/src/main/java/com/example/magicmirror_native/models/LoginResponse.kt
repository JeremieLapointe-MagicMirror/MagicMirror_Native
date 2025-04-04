package com.example.magicmirror_native.models

data class LoginResponse(
    val token: String,
    val user: User
)
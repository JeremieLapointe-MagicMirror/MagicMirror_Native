package com.example.magicmirror_native.models

data class User(
    val id: Int,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val isAdmin: Boolean = false
) {
    val displayName: String
        get() = firstName ?: lastName ?: email
}

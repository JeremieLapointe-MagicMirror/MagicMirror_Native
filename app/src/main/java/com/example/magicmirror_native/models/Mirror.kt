package com.example.magicmirror_native.models

data class Mirror(
    val id: Int,
    val name: String,
    val isActive: Boolean = false,
    val widgets: List<String> = emptyList(),
    val lastSeen: String = "",
    val ipAddress: String? = null
)
package com.example.magicmirror_native.models

data class MirrorStateUpdate(
    val isActive: Boolean,
    val lastUpdate: Long? = null,
    val screenMode: String? = null
)
package com.example.magicmirror_native.models

enum class ScreenMode {
    AUTOMATIC, // PIR contrôle l'état (par défaut)
    ALWAYS_ON, // Toujours allumé
    ALWAYS_OFF // Toujours éteint (veille)
}
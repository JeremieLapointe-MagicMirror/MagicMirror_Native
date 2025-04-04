// MainActivity.kt
package com.example.magicmirror_native

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.magicmirror_native.models.Mirror
import com.example.magicmirror_native.models.User
import com.example.magicmirror_native.repository.MirrorRepository
import com.example.magicmirror_native.screens.AdminScreen
import com.example.magicmirror_native.screens.LoginScreen
import com.example.magicmirror_native.screens.MirrorDetailScreen
import com.example.magicmirror_native.screens.UserScreen
import com.example.magicmirror_native.ui.theme.MagicMirror_NativeTheme

class MainActivity : ComponentActivity() {
    // Repository
    private lateinit var repository: MirrorRepository

    // États
    private var currentUser by mutableStateOf<User?>(null)
    private var mirrors by mutableStateOf<List<Mirror>>(emptyList())
    private var selectedMirror by mutableStateOf<Mirror?>(null)
    private var isLoading by mutableStateOf(false)
    private var showMirrorDetail by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation du repository
        repository = MirrorRepository(this)

        enableEdgeToEdge()
        setContent {
            MagicMirror_NativeTheme {
                when {
                    currentUser == null -> {
                        // Page de connexion
                        LoginScreen(
                            onLoginClick = { email, password -> loginUser(email, password) },
                            isLoading = isLoading
                        )
                    }
                    showMirrorDetail && selectedMirror != null -> {
                        // Détails d'un miroir
                        MirrorDetailScreen(
                            mirror = selectedMirror!!,
                            isAdmin = currentUser?.isAdmin ?: false,
                            onBackClick = { showMirrorDetail = false }
                        )
                    }
                    currentUser?.isAdmin == true -> {
                        // Écran administrateur
                        AdminScreen(
                            user = currentUser!!,
                            mirrors = mirrors,
                            onLogoutClick = { logoutUser() },
                            onMirrorClick = { mirror ->
                                selectedMirror = mirror
                                showMirrorDetail = true
                            }
                        )
                    }
                    else -> {
                        // Écran utilisateur standard
                        UserScreen(
                            user = currentUser!!,
                            mirrors = mirrors,
                            onLogoutClick = { logoutUser() },
                            onMirrorClick = { mirror ->
                                selectedMirror = mirror
                                showMirrorDetail = true
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentUser != null) {
            loadMirrors()
        }
    }

    private fun loginUser(email: String, password: String) {
        isLoading = true

        repository.login(
            email = email,
            password = password,
            onSuccess = { user ->
                currentUser = user
                isLoading = false
                loadMirrors()

                val message = if (user.isAdmin) {
                    "Connecté en tant qu'administrateur"
                } else {
                    "Bienvenue ${user.displayName}!"
                }

                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                isLoading = false
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun loadMirrors() {
        isLoading = true

        repository.getMirrors(
            onSuccess = { mirrorList ->
                mirrors = mirrorList
                isLoading = false
            },
            onError = { errorMessage ->
                isLoading = false
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun logoutUser() {
        repository.logout()
        currentUser = null
        mirrors = emptyList()
        selectedMirror = null
        showMirrorDetail = false
        Toast.makeText(this, "Vous avez été déconnecté", Toast.LENGTH_SHORT).show()
    }
}
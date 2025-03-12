package com.example.magicmirror_native

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.magicmirror_native.ui.theme.MagicMirror_NativeTheme
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private lateinit var requestQueue: RequestQueue
    private var isLoggedIn by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de Volley
        requestQueue = Volley.newRequestQueue(this)

        enableEdgeToEdge()
        setContent {
            MagicMirror_NativeTheme {
                if (isLoggedIn) {
                    MirrorMainPage()
                } else {
                    LoginScreen(
                        onLoginClick = { email, password -> loginUser(email, password) }
                    )
                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        // Préparation des données JSON pour la requête
        val jsonBody = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        // URL de l'API
        val url = "https://magicmirrorapi.jeremielapointe.ca/api/users/login"

        // Création de la requête
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                // Réponse réussie
                try {
                    val token = response.getString("token")
                    saveAuthToken(token)

                    Toast.makeText(
                        this@MainActivity,
                        "Connexion réussie",
                        Toast.LENGTH_SHORT
                    ).show()

                    isLoggedIn = true
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Erreur de traitement de la réponse: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            { error ->
                // Erreur de la requête
                val errorMessage = when (error.networkResponse?.statusCode) {
                    401 -> "Email ou mot de passe incorrect"
                    else -> "Erreur: ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )

        // Ajout de la requête à la file d'attente
        requestQueue.add(jsonObjectRequest)
    }

    private fun saveAuthToken(token: String) {
        // Sauvegarde du token dans les SharedPreferences
        val sharedPreferences = getSharedPreferences("MagicMirrorPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("AUTH_TOKEN", token)
            apply()
        }
    }
}
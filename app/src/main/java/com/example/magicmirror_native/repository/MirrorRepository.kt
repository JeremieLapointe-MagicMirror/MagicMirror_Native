// repository/MirrorRepository.kt
package com.example.magicmirror_native.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.magicmirror_native.api.RetrofitClient
import com.example.magicmirror_native.models.LoginRequest
import com.example.magicmirror_native.models.LoginResponse
import com.example.magicmirror_native.models.Mirror
import com.example.magicmirror_native.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import com.example.magicmirror_native.models.MirrorStateUpdate
import com.example.magicmirror_native.models.ScreenMode

class MirrorRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MagicMirrorPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN_KEY = "AUTH_TOKEN"
    }

    fun login(
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            onError("Veuillez remplir tous les champs")
            return
        }

        val loginRequest = LoginRequest(email, password)

        RetrofitClient.instance.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        saveAuthToken(it.token)
                        onSuccess(it.user)
                    } ?: onError("Réponse vide du serveur")
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Email ou mot de passe incorrect"
                        else -> "Erreur: ${response.message()}"
                    }
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onError("Erreur de connexion: ${t.message}")
            }
        })
    }

    fun getMirrors(
        onSuccess: (List<Mirror>) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = getAuthToken()
        if (token.isNullOrEmpty()) {
            onError("Non authentifié")
            return
        }

        val authHeader = "Bearer $token"

        RetrofitClient.instance.getMirrors(authHeader).enqueue(object : Callback<List<Mirror>> {
            override fun onResponse(call: Call<List<Mirror>>, response: Response<List<Mirror>>) {
                if (response.isSuccessful) {
                    val mirrors = response.body()
                    mirrors?.let {
                        onSuccess(it)
                    } ?: onError("Aucun miroir trouvé")
                } else {
                    onError("Erreur: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Mirror>>, t: Throwable) {
                onError("Erreur de connexion: ${t.message}")
            }
        })
    }

    fun getMirrorById(
        mirrorId: Int,
        onSuccess: (Mirror) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = getAuthToken()
        if (token.isNullOrEmpty()) {
            onError("Non authentifié")
            return
        }

        val authHeader = "Bearer $token"

        RetrofitClient.instance.getMirrorById(authHeader, mirrorId).enqueue(object : Callback<Mirror> {
            override fun onResponse(call: Call<Mirror>, response: Response<Mirror>) {
                if (response.isSuccessful) {
                    val mirror = response.body()
                    mirror?.let {
                        onSuccess(it)
                    } ?: onError("Miroir non trouvé")
                } else {
                    onError("Erreur: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Mirror>, t: Throwable) {
                onError("Erreur de connexion: ${t.message}")
            }
        })
    }
    // repository/MirrorRepository.kt
    fun updateMirrorScreenState(
        mirrorId: Int,
        isScreenOpen: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val token = getAuthToken()
        if (token.isNullOrEmpty()) {
            onError("Non authentifié")
            return
        }

        val authHeader = "Bearer $token"

        // Créer l'objet de mise à jour avec la classe dédiée
        val updateData = if (isScreenOpen) {
            MirrorStateUpdate(isActive = isScreenOpen, lastUpdate = Date().time)
        } else {
            MirrorStateUpdate(isActive = isScreenOpen)
        }

        // Appel API
        RetrofitClient.instance.updateMirrorState(authHeader, mirrorId, updateData)
            .enqueue(object : Callback<Mirror> {
                override fun onResponse(call: Call<Mirror>, response: Response<Mirror>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onError("Erreur: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Mirror>, t: Throwable) {
                    onError("Erreur réseau: ${t.message}")
                }
            })
    }

    fun logout() {
        with(sharedPreferences.edit()) {
            remove(AUTH_TOKEN_KEY)
            apply()
        }
    }

    private fun saveAuthToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(AUTH_TOKEN_KEY, token)
            apply()
        }
    }

    private fun getAuthToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null)
    }
    fun updateMirrorScreenMode(
        mirrorId: Int,
        screenMode: ScreenMode,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val token = getAuthToken()
        if (token.isNullOrEmpty()) {
            onError("Non authentifié")
            return
        }

        val authHeader = "Bearer $token"

        // En fonction du mode, définir aussi l'état
        val isActive = when(screenMode) {
            ScreenMode.AUTOMATIC -> false // Laissez le PIR décider
            ScreenMode.ALWAYS_ON -> true
            ScreenMode.ALWAYS_OFF -> false
        }

        // Créer l'objet de mise à jour
        val updateData = MirrorStateUpdate(
            isActive = isActive,
            screenMode = screenMode.name
        )

        // Appel API
        RetrofitClient.instance.updateMirrorState(authHeader, mirrorId, updateData)
            .enqueue(object : Callback<Mirror> {
                override fun onResponse(call: Call<Mirror>, response: Response<Mirror>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onError("Erreur: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Mirror>, t: Throwable) {
                    onError("Erreur réseau: ${t.message}")
                }
            })
    }
}
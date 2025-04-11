// src/main/java/com/example/magicmirror_native/mqtt/MQTTService.kt
package com.example.magicmirror_native.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject
import java.util.UUID
import javax.net.ssl.SSLContext

class MQTTService(private val context: Context) {
    private val TAG = "MQTTService"
    private var mqttClient: MqttClient? = null

    // Informations de connexion
    private val serverURI = "ssl://mirrormqtt.jeremielapointe.ca:8883"
    private val username = "MirrorMQTT"
    private val password = "Patate123"
    private val temperatureTopic = "serial/temperature"
    private val pirStateTopic = "serial/etatpir"

    // Callbacks pour les mises à jour
    private var temperatureCallback: ((Float) -> Unit)? = null
    private var pirStateCallback: ((Boolean) -> Unit)? = null

    // Initialiser et connecter au broker MQTT
    fun connect(onConnected: () -> Unit, onError: (String) -> Unit) {
        try {
            // Créer un ID client unique
            val clientId = "AndroidClient-" + UUID.randomUUID().toString()

            // Créer le client MQTT (version non-Android)
            mqttClient = MqttClient(serverURI, clientId, null)

            // Définir les options de connexion (avec authentification)
            val options = MqttConnectOptions().apply {
                isCleanSession = true
                userName = username
                password = this@MQTTService.password.toCharArray()
                // Désactiver la vérification du certificat pour simplifier
                sslProperties = java.util.Properties().apply {
                    setProperty("com.ibm.ssl.protocol", "TLSv1.2")
                }
            }

            // Définir les callbacks
            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.e(TAG, "Connexion MQTT perdue", cause)
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    message?.let {
                        when(topic) {
                            temperatureTopic -> {
                                try {
                                    // Parser le message JSON
                                    val payload = String(it.payload)
                                    Log.d(TAG, "Message température reçu: $payload")
                                    val jsonObject = JSONObject(payload)

                                    // Extraire la température
                                    val temperature = jsonObject.getDouble("temperature").toFloat()

                                    // Notifier via le callback
                                    temperatureCallback?.invoke(temperature)

                                    Log.d(TAG, "Température reçue: $temperature°C")
                                } catch (e: Exception) {
                                    Log.e(TAG, "Erreur lors du parsing du message température", e)
                                }
                            }
                            pirStateTopic -> {
                                try {
                                    // Parser le message JSON
                                    val payload = String(it.payload)
                                    Log.d(TAG, "Message PIR reçu: $payload")
                                    val jsonObject = JSONObject(payload)

                                    // Extraire l'état du mouvement
                                    val isMotionDetected = jsonObject.getBoolean("motion_detected")

                                    // Notifier via le callback
                                    pirStateCallback?.invoke(isMotionDetected)

                                    Log.d(TAG, "Mouvement détecté: $isMotionDetected")
                                } catch (e: Exception) {
                                    Log.e(TAG, "Erreur lors du parsing du message PIR", e)
                                }
                            }
                            else -> {
                                // Gestion des autres topics ou topic inconnu
                                Log.d(TAG, "Message reçu sur topic non traité: $topic")
                            }
                        }
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // Non utilisé pour la réception
                }
            })

            // Connexion dans un thread séparé pour éviter les erreurs NetworkOnMainThread
            Thread {
                try {
                    mqttClient?.connect(options)

                    // S'abonner aux topics
                    subscribeToTopics()

                    Log.d(TAG, "Connexion MQTT réussie")

                    // Notification sur le thread principal
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onConnected()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Échec de connexion MQTT", e)

                    // Notification sur le thread principal
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onError("Échec de connexion au broker MQTT: ${e.message}")
                    }
                }
            }.start()

        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'initialisation MQTT", e)
            onError("Erreur MQTT: ${e.message}")
        }
    }

    // S'abonner aux topics
    private fun subscribeToTopics() {
        try {
            mqttClient?.subscribe(temperatureTopic, 1)
            mqttClient?.subscribe(pirStateTopic, 1)
            Log.d(TAG, "Abonnements aux topics réussis")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors des abonnements", e)
        }
    }

    // Définir le callback pour les mises à jour de température
    fun setTemperatureCallback(callback: (Float) -> Unit) {
        this.temperatureCallback = callback
    }

    // Définir le callback pour les mises à jour d'état PIR
    fun setPirStateCallback(callback: (Boolean) -> Unit) {
        this.pirStateCallback = callback
    }

    // Se déconnecter proprement
    fun disconnect() {
        try {
            mqttClient?.disconnect()
            mqttClient = null
            Log.d(TAG, "Déconnexion MQTT")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la déconnexion MQTT", e)
        }
    }
}
package com.example.magicmirror_native

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.magicmirror_native.ui.theme.MagicMirror_NativeTheme
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.*

@Composable
fun MirrorMainPage() {
    val context = LocalContext.current
    var lastMessage by remember { mutableStateOf("En attente de messages...") }

    // Configuration MQTT et abonnement
    DisposableEffect(key1 = Unit) {
        // Création du client MQTT
        val clientId = "MagicMirrorAndroid-" + UUID.randomUUID().toString()
        val persistence = MemoryPersistence()
        val mqttClient = MqttClient(
            "tcp://mirrormqtt.jeremielapointe.ca:1883",
            clientId,
            persistence
        )

        val options = MqttConnectOptions().apply {
            userName = "MirrorMQTT"
            password = "Patate123".toCharArray()
            isCleanSession = true
            connectionTimeout = 30
            keepAliveInterval = 60
        }

        try {
            // Configurer le callback
            mqttClient.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    lastMessage = "Connexion MQTT perdue"
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    message?.let {
                        val payload = String(it.payload)
                        lastMessage = payload
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // Non utilisé ici car nous ne publions pas de messages
                }
            })

            // Se connecter et s'abonner
            mqttClient.connect(options)
            mqttClient.subscribe("test/topic", 0)

        } catch (e: Exception) {
            lastMessage = "Erreur MQTT: ${e.message}"
        }

        // Nettoyage lors de la destruction du composable
        onDispose {
            try {
                if (mqttClient.isConnected) {
                    mqttClient.disconnect()
                }
            } catch (e: Exception) {
                // Gérer l'erreur de déconnexion
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vous êtes connecté!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Dernier message MQTT:",
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = lastMessage,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
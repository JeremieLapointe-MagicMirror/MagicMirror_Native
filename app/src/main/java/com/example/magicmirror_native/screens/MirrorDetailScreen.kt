// screens/MirrorDetailScreen.kt
package com.example.magicmirror_native.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.magicmirror_native.models.Mirror
import com.example.magicmirror_native.models.ScreenMode
import com.example.magicmirror_native.mqtt.MQTTService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MirrorDetailScreen(
    mirror: Mirror,
    isAdmin: Boolean,
    onBackClick: () -> Unit,
    onScreenStateChange: (Int, Boolean) -> Unit = { _, _ -> },
    onScreenModeChange: (Int, ScreenMode) -> Unit = { _, _ -> }
) {
    val backgroundColor = if (isAdmin) Color(0xFFF0F4F8) else Color.White
    var isLightOn by remember { mutableStateOf(false) }

    // États pour MQTT et affichage
    var temperature by remember { mutableStateOf(0f) }
    var isScreenOpen by remember { mutableStateOf(mirror.isActive) }
    var lastUpdateTime by remember { mutableStateOf(mirror.lastSeen) }

    // Mode d'écran (automatique, toujours allumé, veille)
    var screenMode by remember { mutableStateOf(mirror.getScreenMode()) }

    // Contexte pour le service MQTT
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Toast pour le statut de connexion
    var showConnectionToast by remember { mutableStateOf(false) }

    // Effet pour configurer MQTT au montage du composant
    DisposableEffect(key1 = mirror.id) {
        // Créer et connecter le service MQTT
        val mqttService = MQTTService(context)

        // Définir le callback pour les mises à jour de température
        mqttService.setTemperatureCallback { temp ->
            temperature = temp
        }

        // Définir le callback pour les mises à jour d'état PIR
        mqttService.setPirStateCallback { motionDetected ->
            // Ne mettre à jour l'état de l'écran que si le mode est AUTOMATIC
            if (screenMode == ScreenMode.AUTOMATIC) {
                val previousState = isScreenOpen
                isScreenOpen = motionDetected

                // Si l'état change de fermé à ouvert, mettre à jour la dernière utilisation
                if (!previousState && motionDetected) {
                    val currentDateTime = SimpleDateFormat("MM/dd/yyyy, HH:mm:ss", Locale.getDefault())
                        .format(Date())
                    lastUpdateTime = currentDateTime

                    // Appeler l'API pour mettre à jour l'état du miroir
                    scope.launch {
                        onScreenStateChange(mirror.id, true)
                    }
                } else if (previousState && !motionDetected) {
                    // Si l'état change d'ouvert à fermé
                    scope.launch {
                        onScreenStateChange(mirror.id, false)
                    }
                }
            }
        }

        // Connexion au broker
        mqttService.connect(
            onConnected = {
                scope.launch {
                    showConnectionToast = true
                }
            },
            onError = { errorMsg ->
                scope.launch {
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Nettoyage lors du démontage
        onDispose {
            mqttService.disconnect()
        }
    }

    // Afficher le toast de connexion
    if (showConnectionToast) {
        Toast.makeText(context, "Connexion MQTT établie", Toast.LENGTH_SHORT).show()
        showConnectionToast = false
    }

    // Mettre à jour l'état d'affichage en fonction du mode d'écran
    LaunchedEffect(screenMode) {
        when (screenMode) {
            ScreenMode.ALWAYS_ON -> {
                isScreenOpen = true
            }
            ScreenMode.ALWAYS_OFF -> {
                isScreenOpen = false
            }
            else -> { /* En mode AUTOMATIC, l'état est géré par le PIR */ }
        }
    }

    // Déterminer les couleurs en fonction de la température
    val temperatureColor = when {
        temperature > 50 -> Color.Red
        temperature > 40 -> Color(0xFFFFD700)  // Jaune
        else -> MaterialTheme.colorScheme.primary
    }

    val showWarningIcon = temperature > 40

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Header avec bouton retour
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour"
                )
            }

            Text(
                text = mirror.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // État du miroir - MODIFIÉ pour afficher l'état de l'écran
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "État de l'écran",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isScreenOpen) "Ouvert" else "Fermé",
                    color = if (isScreenOpen) Color.Green else Color.Red
                )
            }

            Text(
                text = "Dernière utilisation: $lastUpdateTime",
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // Température du système
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            // Changer la couleur de fond si >50°C
            colors = CardDefaults.cardColors(
                containerColor = if (temperature > 50) Color(0xFFFFEEEE) else MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icône de température
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Température",
                    tint = temperatureColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Température du système",
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.weight(1f))

                // Icône d'avertissement si la température est élevée
                if (showWarningIcon) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Avertissement Température",
                        tint = temperatureColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))
                }

                // Valeur de température en temps réel via MQTT
                Text(
                    text = if (temperature > 0) "${temperature.toInt()}°C" else "En attente...",
                    fontWeight = FontWeight.Bold,
                    color = temperatureColor
                )
            }
        }

        // Widgets actifs
        Text(
            text = "Widgets Actifs",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            mirror.widgets.take(4).forEach { widget ->
                WidgetItem(name = widget)
            }
        }

        // Paramètres rapides
        Text(
            text = "Paramètres Rapides",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )

        // Luminosité
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Luminosité",
                fontSize = 14.sp
            )

            Slider(
                value = 0.75f,  // Exemple: 75%
                onValueChange = { /* */ },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Bouton Allumer/Éteindre lumière
        Button(
            onClick = { isLightOn = !isLightOn },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLightOn) MaterialTheme.colorScheme.primary else Color.Gray
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Lumière",
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isLightOn) "Éteindre la lumière" else "Allumer la lumière",
                    color = Color.White
                )
            }
        }

        // Section Mode d'écran
        Text(
            text = "Mode d'écran",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        // Option Automatique (PIR)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = screenMode == ScreenMode.AUTOMATIC,
                onClick = {
                    screenMode = ScreenMode.AUTOMATIC
                    // Appeler l'API pour mettre à jour le mode
                    scope.launch {
                        onScreenModeChange(mirror.id, ScreenMode.AUTOMATIC)
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Automatique (capteur de mouvement)")
        }

        // Option Toujours allumé
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = screenMode == ScreenMode.ALWAYS_ON,
                onClick = {
                    screenMode = ScreenMode.ALWAYS_ON
                    // Appeler l'API pour mettre à jour le mode
                    scope.launch {
                        onScreenModeChange(mirror.id, ScreenMode.ALWAYS_ON)
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Toujours allumé")
        }

        // Option Toujours éteint (veille)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = screenMode == ScreenMode.ALWAYS_OFF,
                onClick = {
                    screenMode = ScreenMode.ALWAYS_OFF
                    // Appeler l'API pour mettre à jour le mode
                    scope.launch {
                        onScreenModeChange(mirror.id, ScreenMode.ALWAYS_OFF)
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mode veille (toujours éteint)")
        }
    }
}

@Composable
fun WidgetItem(name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        // Cercle représentant le widget
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(25.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = name,
            fontSize = 12.sp
        )

        Text(
            text = "Activé",
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}
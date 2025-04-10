// screens/MirrorDetailScreen.kt
package com.example.magicmirror_native.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info  // Icône disponible par défaut
import androidx.compose.material.icons.filled.Settings  // Icône disponible par défaut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.magicmirror_native.models.Mirror

@Composable
fun MirrorDetailScreen(
    mirror: Mirror,
    isAdmin: Boolean,
    onBackClick: () -> Unit
) {
    val backgroundColor = if (isAdmin) Color(0xFFF0F4F8) else Color.White
    var isLightOn by remember { mutableStateOf(false) }

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

        // État du miroir
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
                    text = "État du Miroir",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (mirror.isActive) "Connecté" else "Déconnecté",
                    color = if (mirror.isActive) Color.Green else Color.Red
                )
            }

            Text(
                text = "Dernière utilisation: ${mirror.lastSeen}",
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
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,  // Icône Info à la place de Thermostat
                    contentDescription = "Température",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Température du système",
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "42°C", // Valeur d'exemple
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
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
                    imageVector = Icons.Default.Settings,  // Icône Settings à la place de Lightbulb
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

        // Mode Veille
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mode Veille",
                fontSize = 14.sp
            )

            Switch(
                checked = false,
                onCheckedChange = { /* */ }
            )
        }
    }
}

@Composable
fun WidgetItem(name: String) {
    // Code inchangé
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
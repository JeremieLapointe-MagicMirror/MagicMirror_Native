package com.example.magicmirror_native.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
        
        // Volume
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Volume",
                fontSize = 14.sp
            )
            
            Slider(
                value = 0.5f,  // Exemple: 50%
                onValueChange = { /* */ },
                modifier = Modifier.fillMaxWidth()
            )
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
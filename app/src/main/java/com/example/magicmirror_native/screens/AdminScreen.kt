// screens/AdminScreen.kt
package com.example.magicmirror_native.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.magicmirror_native.R
import com.example.magicmirror_native.components.SearchBar
import com.example.magicmirror_native.models.Mirror
import com.example.magicmirror_native.models.User

@Composable
fun AdminScreen(
    user: User,
    mirrors: List<Mirror>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onMirrorClick: (Mirror) -> Unit,
    onFilterChanged: (String?) -> Unit
) {
    var currentFilter by remember { mutableStateOf<String?>(null) }

    // Utiliser Box pour superposer le contenu sur l'image de fond
    Box(modifier = Modifier.fillMaxSize()) {
        // Image de fond admin.png
        Image(
            painter = painterResource(id = R.drawable.admin),
            contentDescription = "Admin Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Pour remplir tout l'écran
        )

        // Contenu de l'écran avec un fond semi-transparent pour améliorer la lisibilité
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header - Admin version
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1976D2))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bonjour Admin: ${user.displayName}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Déconnexion",
                            tint = Color.White
                        )
                    }
                }
            }

            // Barre de recherche avec fond blanc pour visibilité
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                SearchBar(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = "Rechercher un miroir..."
                )
            }

            // Card pour les filtres avec fond blanc pour visibilité
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                // Filtres
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tous",
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .clickable {
                                currentFilter = null
                                onFilterChanged(null)
                            },
                        fontWeight = if (currentFilter == null) FontWeight.Bold else FontWeight.Normal,
                        color = if (currentFilter == null) MaterialTheme.colorScheme.primary else Color.Gray
                    )

                    Text(
                        text = "Ouverts",
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .clickable {
                                currentFilter = "active"
                                onFilterChanged("active")
                            },
                        fontWeight = if (currentFilter == "active") FontWeight.Bold else FontWeight.Normal,
                        color = if (currentFilter == "active") MaterialTheme.colorScheme.primary else Color.Gray
                    )

                    Text(
                        text = "Fermés",
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .clickable {
                                currentFilter = "inactive"
                                onFilterChanged("inactive")
                            },
                        fontWeight = if (currentFilter == "inactive") FontWeight.Bold else FontWeight.Normal,
                        color = if (currentFilter == "inactive") MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }

            // Liste des miroirs
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                if (mirrors.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun miroir trouvé",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(mirrors) { mirror ->
                            MirrorItem(mirror, onMirrorClick)
                        }
                    }
                }
            }
        }
    }
}
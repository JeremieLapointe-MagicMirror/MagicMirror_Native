// screens/AdminScreen.kt
package com.example.magicmirror_native.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
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
                    text = "Admin: ${user.displayName}",
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

        // Barre de recherche
        SearchBar(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = "Rechercher un miroir..."
        )

        // Filtres
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
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
                text = "Ouverts",  // Changé de "Actifs" à "Ouverts"
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
                text = "Fermés",  // Changé de "Inactifs" à "Fermés"
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

        // Liste des miroirs
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
                modifier = Modifier.fillMaxWidth()
            ) {
                items(mirrors) { mirror ->
                    MirrorItem(mirror, onMirrorClick)
                }
            }
        }
    }
}
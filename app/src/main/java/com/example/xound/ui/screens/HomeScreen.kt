package com.example.xound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xound.ui.theme.XoundNavy
import com.example.xound.ui.theme.XoundYellow

private val XoundCream = Color(0xFFF5F0E8)

@Composable
fun HomeScreen(onLogout: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(XoundCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 24.dp)
    ) {
        // Logout button
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onLogout,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    tint = XoundNavy,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Greeting
        Text(
            text = "¡Hola de nuevo!,",
            fontSize = 14.sp,
            color = Color.Black
        )
        Text(
            text = "Josseph Peralta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = XoundYellow
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard("24", "Canciones", Modifier.weight(1f))
            StatCard("6", "Tonalidades", Modifier.weight(1f))
            StatCard("3", "Eventos", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action grid - fila 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionCard(
                title = "Modo en Vivo",
                subtitle = "Toca ahora",
                icon = Icons.Default.MusicNote,
                backgroundColor = XoundYellow,
                contentColor = XoundNavy,
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                title = "Nuevo Evento",
                subtitle = "Crear set list",
                icon = Icons.Default.Event,
                backgroundColor = XoundNavy,
                contentColor = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action grid - fila 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionCard(
                title = "Biblioteca",
                subtitle = "24 canciones",
                icon = Icons.Default.LibraryMusic,
                backgroundColor = XoundNavy,
                contentColor = Color.White,
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                title = "Agregar",
                subtitle = "Nueva canción",
                icon = Icons.Default.Add,
                backgroundColor = XoundNavy,
                contentColor = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección Recientes
        Text(
            text = "RECIENTES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF888888),
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        RecentSongItem(
            title = "Concierto 1",
            keyLabel = "La Mayor",
            keyColor = Color(0xFFE8934A),
            duration = "2:35",
            iconColor = Color(0xFFE05A5A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        RecentSongItem(
            title = "Concierto 2",
            keyLabel = "Fa Mayor",
            keyColor = Color(0xFF4CAF50),
            duration = "2:35",
            iconColor = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(8.dp))

        RecentSongItem(
            title = "Concierto 3",
            keyLabel = "Mi Menor",
            keyColor = Color(0xFF3A4B7A),
            duration = "2:38",
            iconColor = Color(0xFF3A4B7A)
        )
    }
}

@Composable
private fun StatCard(number: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = XoundNavy)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp)
        ) {
            Text(
                text = number,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun RecentSongItem(
    title: String,
    keyLabel: String,
    keyColor: Color,
    duration: String,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = XoundNavy)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono circular de instrumento
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Título, tonalidad y duración
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(keyColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = keyLabel,
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = duration,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Botón de reproducción
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(XoundYellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Reproducir",
                    tint = XoundNavy,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

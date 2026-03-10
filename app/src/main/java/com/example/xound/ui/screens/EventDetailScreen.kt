package com.example.xound.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xound.data.model.EventResponse
import com.example.xound.data.model.SetlistSongResponse
import com.example.xound.data.model.SongResponse
import com.example.xound.ui.theme.XoundNavy
import com.example.xound.ui.theme.XoundYellow
import com.example.xound.ui.viewmodel.EventViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val XoundCream = Color(0xFFF5F0E8)

private val instrumentColors = listOf(
    Color(0xFF7B2D8E),
    Color(0xFF2D5F8E),
    Color(0xFF8E2D2D),
    Color(0xFF2D8E5F),
    Color(0xFF8E6B2D),
    Color(0xFF4A2D8E)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: EventResponse,
    onBack: () -> Unit = {},
    onAddSongToSetlist: () -> Unit = {},
    eventViewModel: EventViewModel
) {
    val setlistSongs by eventViewModel.setlistSongs.collectAsState()
    val setlistLoading by eventViewModel.setlistLoading.collectAsState()

    var songToRemove by remember { mutableStateOf<SetlistSongResponse?>(null) }

    LaunchedEffect(event.id) {
        eventViewModel.fetchSetlist(event.id)
    }

    val formattedDate = formatDetailDate(event.eventDate)

    // Remove confirmation dialog
    if (songToRemove != null) {
        AlertDialog(
            onDismissRequest = { songToRemove = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Quitar del setlist",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Quitar \"${songToRemove?.song?.title ?: "esta canción"}\" del setlist?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        songToRemove?.let {
                            eventViewModel.removeSongFromSetlist(event.id, it.songId)
                        }
                        songToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Quitar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { songToRemove = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(XoundCream)
            .padding(top = 48.dp)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.offset(x = (-12).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = XoundNavy
                        )
                    }

                    Text(
                        text = event.title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = XoundYellow
                    )
                }

                // Add song button
                IconButton(
                    onClick = onAddSongToSetlist,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(40.dp)
                        .background(XoundYellow, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar canción al setlist",
                        tint = XoundNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Venue + Date
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!event.venue.isNullOrBlank()) {
                    Text(
                        text = event.venue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = XoundYellow
                    )
                    Text(
                        text = "  ",
                        fontSize = 14.sp,
                        color = Color(0xFF888888)
                    )
                }
                Text(
                    text = formattedDate,
                    fontSize = 13.sp,
                    color = Color(0xFF888888)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${setlistSongs.size} canciones",
                fontSize = 13.sp,
                color = Color(0xFF888888)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Publicar + Compartir buttons
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { eventViewModel.togglePublishFromDetail(event.id) },
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = null,
                        tint = XoundNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (event.published) "Despublicar" else "Publicar",
                        color = XoundNavy,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (event.shareCode != null) {
                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = XoundNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Compartir",
                            color = XoundNavy,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Setlist
        if (setlistLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = XoundNavy,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(36.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(setlistSongs, key = { it.id }) { setlistItem ->
                    SwipeableSetlistCard(
                        setlistItem = setlistItem,
                        colorIndex = (setlistItem.songId % instrumentColors.size).toInt(),
                        onRemove = { songToRemove = setlistItem }
                    )
                }

                if (setlistSongs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay canciones en el setlist",
                                color = Color(0xFF888888),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableSetlistCard(
    setlistItem: SetlistSongResponse,
    colorIndex: Int,
    onRemove: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove()
            }
            false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val backgroundColor by animateColorAsState(
                targetValue = if (direction == SwipeToDismissBoxValue.EndToStart) Color(0xFFE53935)
                else Color.Transparent,
                label = "swipeBg"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (direction == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        SetlistSongCard(
            setlistItem = setlistItem,
            colorIndex = colorIndex
        )
    }
}

@Composable
private fun SetlistSongCard(setlistItem: SetlistSongResponse, colorIndex: Int) {
    val song = setlistItem.song

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = XoundNavy)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Instrument icon placeholder
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(instrumentColors[colorIndex].copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = XoundYellow,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song?.title ?: "Canción ${setlistItem.songId}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!song?.tone.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .background(XoundYellow, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = song!!.tone!!,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = XoundNavy
                            )
                        }
                    }
                    if (song?.bpm != null) {
                        Text(
                            text = "${song.bpm} BPM",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

// Dialog to pick songs to add to setlist
@Composable
fun AddSongToSetlistDialog(
    songs: List<SongResponse>,
    setlistSongIds: Set<Long>,
    onAdd: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Agregar al setlist",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            val available = songs.filter { it.id !in setlistSongIds }
            if (available.isEmpty()) {
                Text("No hay canciones disponibles para agregar")
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(available, key = { it.id }) { song ->
                        Card(
                            onClick = { onAdd(song.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = XoundNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = song.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (!song.artist.isNullOrBlank()) {
                                        Text(
                                            text = song.artist,
                                            fontSize = 12.sp,
                                            color = Color(0xFF888888),
                                            maxLines = 1
                                        )
                                    }
                                }
                                if (!song.tone.isNullOrBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .background(XoundYellow, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = song.tone,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = XoundNavy
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = XoundNavy)
            }
        }
    )
}

private fun formatDetailDate(dateStr: String?): String {
    if (dateStr.isNullOrBlank()) return ""
    return try {
        val date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy - HH:mm 'h'", Locale("es", "MX"))
        date.format(formatter).uppercase()
    } catch (_: Exception) {
        dateStr
    }
}

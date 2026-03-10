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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.xound.data.network.CoverArtService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xound.data.model.SongResponse
import com.example.xound.ui.theme.XoundNavy
import com.example.xound.ui.theme.XoundYellow
import com.example.xound.ui.viewmodel.SongViewModel

private val XoundCream = Color(0xFFF5F0E8)

private val thumbnailColors = listOf(
    Color(0xFF7B2D8E),
    Color(0xFF2D5F8E),
    Color(0xFF8E2D2D),
    Color(0xFF2D8E5F),
    Color(0xFF8E6B2D),
    Color(0xFF4A2D8E)
)

@Composable
fun LibraryScreen(
    onBack: () -> Unit = {},
    onAddSong: () -> Unit = {},
    onEditSong: (SongResponse) -> Unit = {},
    songViewModel: SongViewModel = viewModel()
) {
    val songs by songViewModel.songs.collectAsState()
    val favorites by songViewModel.favorites.collectAsState()
    val isLoading by songViewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todas") }
    val filters = listOf("Todas", "Favoritos", "Recientes", "Por clave")

    // Delete confirmation dialog
    var songToDelete by remember { mutableStateOf<SongResponse?>(null) }

    LaunchedEffect(Unit) {
        songViewModel.fetchSongs()
        songViewModel.fetchFavorites()
    }

    val filteredSongs = remember(songs, favorites, selectedFilter) {
        when (selectedFilter) {
            "Favoritos" -> songs.filter { favorites.contains(it.id) }
            "Recientes" -> songs.sortedByDescending { it.createdAt }
            "Por clave" -> songs.sortedBy { it.tone ?: "ZZZ" }
            else -> songs
        }
    }

    // Delete confirmation alert
    if (songToDelete != null) {
        AlertDialog(
            onDismissRequest = { songToDelete = null },
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
                    text = "Eliminar cancion",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Estás seguro de que quieres eliminar la cancion \"${songToDelete?.title}\"?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        songToDelete?.let { songViewModel.deleteSong(it.id) }
                        songToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Elimar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { songToDelete = null }) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
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
                    text = "Biblioteca",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = XoundYellow
                )
                Text(
                    text = "${songs.size} canciones",
                    fontSize = 14.sp,
                    color = Color(0xFF888888)
                )
            }

            IconButton(
                onClick = onAddSong,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(40.dp)
                    .background(XoundYellow, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar canción",
                    tint = XoundNavy,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                songViewModel.searchSongs(it)
            },
            placeholder = { Text("Search", color = Color(0xFF999999)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF999999),
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = XoundNavy,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = XoundNavy,
                        selectedLabelColor = Color.White,
                        containerColor = Color.Transparent,
                        labelColor = XoundNavy
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = XoundNavy,
                        selectedBorderColor = XoundNavy,
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Song list
        if (isLoading) {
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
                items(filteredSongs, key = { it.id }) { song ->
                    SwipeableSongCard(
                        song = song,
                        colorIndex = (song.id % thumbnailColors.size).toInt(),
                        isFavorite = favorites.contains(song.id),
                        onToggleFavorite = { songViewModel.toggleFavorite(song.id) },
                        onDelete = { songToDelete = song },
                        onEdit = { onEditSong(song) }
                    )
                }

                if (filteredSongs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (selectedFilter == "Favoritos") "Sin favoritos aún"
                                else "No hay canciones",
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
private fun SwipeableSongCard(
    song: SongResponse,
    colorIndex: Int,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false // Don't dismiss, show dialog first
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit()
                    false // Don't dismiss, navigate to edit
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection

            val backgroundColor by animateColorAsState(
                targetValue = when (direction) {
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFE53935) // Red for delete
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF2196F3) // Blue for edit
                    else -> Color.Transparent
                },
                label = "swipeBg"
            )

            val icon = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                else -> null
            }

            val alignment = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                else -> Alignment.Center
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true
    ) {
        SongCard(
            song = song,
            colorIndex = colorIndex,
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite
        )
    }
}

@Composable
private fun SongCard(song: SongResponse, colorIndex: Int, isFavorite: Boolean = false, onToggleFavorite: () -> Unit = {}) {
    var coverUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(song.id) {
        coverUrl = CoverArtService.getCoverUrl(song.artist, song.title)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(thumbnailColors[colorIndex]),
                contentAlignment = Alignment.Center
            ) {
                if (coverUrl != null) {
                    AsyncImage(
                        model = coverUrl,
                        contentDescription = song.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Song info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!song.tone.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .background(XoundYellow, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = song.tone,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = XoundNavy
                            )
                        }
                    }
                    if (!song.artist.isNullOrBlank()) {
                        Text(
                            text = song.artist,
                            fontSize = 12.sp,
                            color = Color(0xFF888888),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Favorite button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (isFavorite) Color(0xFFE53935) else Color(0xFFBBBBBB),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

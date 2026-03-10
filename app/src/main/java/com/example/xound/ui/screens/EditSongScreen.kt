package com.example.xound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xound.data.model.CreateSongRequest
import com.example.xound.data.model.SongResponse
import com.example.xound.data.network.RetrofitClient
import com.example.xound.ui.theme.XoundNavy
import com.example.xound.ui.theme.XoundYellow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

private val XoundCream = Color(0xFFF5F0E8)

sealed class EditSongState {
    object Idle : EditSongState()
    object Loading : EditSongState()
    object Success : EditSongState()
    data class Error(val message: String) : EditSongState()
}

class EditSongViewModel : ViewModel() {
    private val _state = MutableStateFlow<EditSongState>(EditSongState.Idle)
    val state: StateFlow<EditSongState> = _state.asStateFlow()

    fun updateSong(id: Long, title: String, artist: String?, tone: String?, bpm: Int?, timeSignature: String?, lyrics: String?) {
        if (title.isBlank()) {
            _state.value = EditSongState.Error("El nombre es requerido")
            return
        }
        viewModelScope.launch {
            _state.value = EditSongState.Loading
            try {
                RetrofitClient.apiService.updateSong(
                    id,
                    CreateSongRequest(
                        title = title.trim(),
                        artist = artist?.trim()?.ifBlank { null },
                        tone = tone?.trim()?.ifBlank { null },
                        bpm = bpm,
                        timeSignature = timeSignature?.trim()?.ifBlank { null },
                        lyrics = lyrics?.trim()?.ifBlank { null }
                    )
                )
                _state.value = EditSongState.Success
            } catch (e: HttpException) {
                val body = e.response()?.errorBody()?.string()
                _state.value = EditSongState.Error("Error ${e.code()}: ${body ?: e.message()}")
            } catch (e: Exception) {
                _state.value = EditSongState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun resetState() {
        _state.value = EditSongState.Idle
    }
}

@Composable
fun EditSongScreen(
    song: SongResponse,
    onBack: () -> Unit = {},
    editSongViewModel: EditSongViewModel = viewModel()
) {
    var title by remember { mutableStateOf(song.title) }
    var artist by remember { mutableStateOf(song.artist ?: "") }
    var tone by remember { mutableStateOf(song.tone ?: "") }
    var bpmText by remember { mutableStateOf(song.bpm?.toString() ?: "") }
    var timeSignature by remember { mutableStateOf(song.timeSignature ?: "") }
    var lyrics by remember { mutableStateOf(song.lyrics ?: "") }

    val state by editSongViewModel.state.collectAsState()
    val isLoading = state is EditSongState.Loading
    val isSuccess = state is EditSongState.Success

    LaunchedEffect(state) {
        if (state is EditSongState.Success) {
            onBack()
            editSongViewModel.resetState()
        }
    }

    DisposableEffect(Unit) {
        onDispose { editSongViewModel.resetState() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(XoundCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 24.dp)
    ) {
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
            text = "Editar Canción",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = XoundYellow
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nombre
        EditLabel("Nombre de la canción")
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            enabled = !isLoading,
            colors = editFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Artista
        EditLabel("Artista")
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = artist,
            onValueChange = { artist = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            enabled = !isLoading,
            colors = editFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tonalidad, BPM, Ritmo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                EditLabel("Tonalidad")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = tone,
                    onValueChange = { tone = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isLoading,
                    colors = editFieldColors()
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                EditLabel("BPM")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = bpmText,
                    onValueChange = { bpmText = it.filter { c -> c.isDigit() } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = editFieldColors()
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                EditLabel("Ritmo")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = timeSignature,
                    onValueChange = { timeSignature = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isLoading,
                    colors = editFieldColors()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Letra
        EditLabel("Letra")
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = lyrics,
            onValueChange = { lyrics = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            maxLines = 20,
            colors = editFieldColors()
        )

        // Error
        if (state is EditSongState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (state as EditSongState.Error).message,
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save button
        Button(
            onClick = {
                editSongViewModel.updateSong(
                    song.id, title, artist, tone,
                    bpmText.toIntOrNull(), timeSignature, lyrics
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = XoundNavy),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Text(
                    text = "Guardar Cambios",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun EditLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black
    )
}

@Composable
private fun editFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = Color(0xFFE5E5E5),
    focusedBorderColor = XoundNavy,
    unfocusedContainerColor = Color.White,
    focusedContainerColor = Color.White
)

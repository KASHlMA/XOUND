package com.example.xound.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xound.data.local.SessionManager
import com.example.xound.data.model.AuthRequest
import com.example.xound.data.model.RegisterRequest
import com.example.xound.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val token: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

/** Extrae un mensaje legible de un HttpException sin exponer JSON al usuario. */
private fun HttpException.friendlyMessage(): String {
    return when (code()) {
        401 -> "Credenciales incorrectas"
        403 -> "No tienes permiso para acceder"
        404 -> "Usuario no encontrado"
        500, 502, 503 -> "Error del servidor, intenta más tarde"
        else -> {
            // Intentar extraer el campo "message" o "error" del body JSON
            runCatching {
                val body = response()?.errorBody()?.string() ?: return@runCatching null
                val json = JSONObject(body)
                json.optString("message").takeIf { it.isNotBlank() }
                    ?: json.optString("error").takeIf { it.isNotBlank() }
            }.getOrNull() ?: "Error inesperado (${code()})"
        }
    }
}

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Username y contraseña son requeridos")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = RetrofitClient.apiService.login(AuthRequest(username.trim(), password))
                val token = response.resolveToken()
                SessionManager.saveSession(
                    token = token,
                    userId = response.user?.id,
                    name = response.user?.name,
                    email = response.user?.email,
                    roleName = response.user?.roleName
                )
                _uiState.value = AuthUiState.Success(token)
            } catch (e: HttpException) {
                _uiState.value = AuthUiState.Error(e.friendlyMessage())
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun register(name: String, username: String, password: String) {
        if (name.isBlank() || username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Todos los campos son requeridos")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(name.trim(), username.trim(), password)
                )
                val token = response.resolveToken()
                SessionManager.saveSession(
                    token = token,
                    userId = response.user?.id,
                    name = response.user?.name ?: name.trim(),
                    email = response.user?.email ?: username.trim(),
                    roleName = response.user?.roleName
                )
                _uiState.value = AuthUiState.Success(token)
            } catch (e: HttpException) {
                _uiState.value = AuthUiState.Error(e.friendlyMessage())
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun logout() {
        SessionManager.clearSession()
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

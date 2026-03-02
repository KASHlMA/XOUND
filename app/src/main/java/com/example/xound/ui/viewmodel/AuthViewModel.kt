package com.example.xound.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xound.data.model.AuthRequest
import com.example.xound.data.model.RegisterRequest
import com.example.xound.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val token: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = RetrofitClient.apiService.login(AuthRequest(email, password))
                _uiState.value = AuthUiState.Success(response.resolveToken())
            } catch (e: HttpException) {
                val body = e.response()?.errorBody()?.string()
                _uiState.value = AuthUiState.Error("Error ${e.code()}: ${body ?: e.message()}")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                RetrofitClient.apiService.register(RegisterRequest(email, password))
                _uiState.value = AuthUiState.Success("")
            } catch (e: HttpException) {
                val body = e.response()?.errorBody()?.string()
                _uiState.value = AuthUiState.Error("Error ${e.code()}: ${body ?: e.message()}")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

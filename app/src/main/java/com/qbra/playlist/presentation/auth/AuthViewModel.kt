package com.qbra.playlist.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qbra.playlist.domain.AuthRepository
import com.qbra.playlist.domain.Resource
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    // Giriş Yapma Fonksiyonu
    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthState(error = "E-posta ve şifre boş bırakılamaz.")
            return
        }

        viewModelScope.launch {
            _state.value = AuthState(isLoading = true)
            when (val result = repository.signIn(email, password)) {
                is Resource.Success -> {
                    _state.value = AuthState(user = result.data, isSuccess = true)
                }
                is Resource.Error -> {
                    _state.value = AuthState(error = result.message ?: "Giriş başarısız.")
                }
                is Resource.Loading -> { }
            }
        }
    }

    // Kayıt Olma Fonksiyonu
    fun signUp(email: String, password: String, username: String) {
        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            _state.value = AuthState(error = "Tüm alanları doldurunuz.")
            return
        }

        viewModelScope.launch {
            _state.value = AuthState(isLoading = true)
            when (val result = repository.signUp(email, password, username)) {
                is Resource.Success -> {
                    _state.value = AuthState(user = result.data, isSuccess = true)
                }
                is Resource.Error -> {
                    _state.value = AuthState(error = result.message ?: "Kayıt başarısız.")
                }
                is Resource.Loading -> { }
            }
        }
    }

    // Çıkış Yapma Fonksiyonu
    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            resetState() // Çıkış yapınca state'i sıfırla
        }
    }

    // Ekranlar arası geçişte state'i sıfırlamak için
    fun resetState() {
        _state.value = AuthState()
    }
}
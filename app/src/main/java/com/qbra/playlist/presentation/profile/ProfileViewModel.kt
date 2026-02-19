package com.qbra.playlist.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qbra.playlist.domain.AuthRepository
import com.qbra.playlist.domain.LogRepository
import com.qbra.playlist.domain.Resource
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val logRepository: LogRepository
) : ViewModel() {

    private val _state = mutableStateOf(ProfileState())
    val state: State<ProfileState> = _state

    // Dışarıdan bir ID gelirse onu gelmezse kendi ID'mizi yükle
    fun loadProfile(targetUserId: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // Dışarıdan ID gelmediyse kendi ID'mizi al
            val uidToLoad = targetUserId ?: authRepository.getCurrentUser()?.uid

            if (uidToLoad == null) {
                _state.value = _state.value.copy(isLoading = false, error = "Kullanıcı bulunamadı.")
                return@launch
            }

            // Profiline girdiğimiz kişinin username'ini çek
            val userResult = authRepository.getUserById(uidToLoad)
            val profileName = if (userResult is Resource.Success) userResult.data?.username ?: "Bilinmeyen" else "Profil"

            // O kişinin logladığı oyunları çekiyoruz
            when (val logsResult = logRepository.getUserLogs(uidToLoad)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        logs = logsResult.data ?: emptyList(),
                        username = profileName
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = logsResult.message ?: "Kayıtlar alınamadı.",
                        username = profileName
                    )
                }
                is Resource.Loading -> { }
            }
        }
    }
}
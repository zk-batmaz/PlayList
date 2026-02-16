package com.qbra.playlist.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qbra.playlist.domain.GameRepository
import com.qbra.playlist.domain.Resource
import kotlinx.coroutines.launch

class GameDetailViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _state = mutableStateOf(GameDetailState())
    val state: State<GameDetailState> = _state

    fun getGameDetail(id: Int) {
        viewModelScope.launch {
            _state.value = GameDetailState(isLoading = true)

            when (val result = repository.getGameDetail(id)) {
                is Resource.Success -> {
                    _state.value = GameDetailState(game = result.data)
                }
                is Resource.Error -> {
                    _state.value = GameDetailState(error = result.message ?: "Detaylar yüklenemedi.")
                }
                is Resource.Loading -> { }
            }
        }
    }
}
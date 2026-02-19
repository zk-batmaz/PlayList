package com.qbra.playlist.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qbra.playlist.domain.AuthRepository
import com.qbra.playlist.domain.GameRepository
import com.qbra.playlist.domain.Resource
import kotlinx.coroutines.launch

class GameViewModel(
    private val repository: GameRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(GameState())
    val state: State<GameState> = _state

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private var currentPage = 1
    private var isPaginating = false

    init {
        getGames("")
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun performSearch() {
        if (_state.value.isSearchModeUsers) {
            searchUsers(_searchQuery.value)
        } else {
            searchGames()
        }
    }

    fun searchGames() {
        currentPage = 1
        _state.value = GameState(isLoading = true)
        getGames(_searchQuery.value)
    }

    fun getGames(query: String) {
        if (isPaginating) return

        viewModelScope.launch {
            isPaginating = true

            val apiQuery = if (query.isNotBlank()) query else null

            when (val result = repository.getGames(apiQuery, currentPage)) {
                is Resource.Success -> {
                    val newGames = result.data ?: emptyList()

                    // eğer 1. sayfadaysak listeyi direkt al değilse eski listenin sonuna yeni listeyi ekle
                    val currentList = if (currentPage == 1) emptyList() else _state.value.games

                    _state.value = GameState(
                        games = currentList + newGames // iki listeyi birleştir
                    )

                    currentPage++
                    isPaginating = false
                }
                is Resource.Error -> {
                    _state.value = GameState(error = result.message ?: "Beklenmeyen bir hata oluştu.")
                    isPaginating = false
                }
                is Resource.Loading -> { }
            }
        }
    }

    fun toggleSearchMode(isUsers: Boolean) {
        _state.value = _state.value.copy(isSearchModeUsers = isUsers)

        if (_searchQuery.value.isNotBlank()) {
            performSearch()
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUserLoading = true)
            when (val result = authRepository.searchUsers(query)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isUserLoading = false,
                        users = result.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isUserLoading = false,
                        error = result.message ?: "Kullanıcılar bulunamadı."
                    )
                }
                is Resource.Loading -> { }
            }
        }
    }
}
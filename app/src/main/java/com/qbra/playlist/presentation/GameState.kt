package com.qbra.playlist.presentation

import com.qbra.playlist.domain.Game
import com.qbra.playlist.domain.User

data class GameState(
    val isLoading: Boolean = false,
    val games: List<Game> = emptyList(),
    val error: String = "",
    val users: List<User> = emptyList(),
    val isSearchModeUsers: Boolean = false,
    val isUserLoading: Boolean = false
)
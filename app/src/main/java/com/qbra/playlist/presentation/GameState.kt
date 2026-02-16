package com.qbra.playlist.presentation

import com.qbra.playlist.domain.Game

data class GameState(
    val isLoading: Boolean = false,
    val games: List<Game> = emptyList(),
    val error: String = ""
)
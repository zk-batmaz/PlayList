package com.qbra.playlist.presentation

import com.qbra.playlist.domain.GameDetail

data class GameDetailState(
    val isLoading: Boolean = false,
    val game: GameDetail? = null,
    val error: String = ""
)

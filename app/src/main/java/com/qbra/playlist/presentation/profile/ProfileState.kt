package com.qbra.playlist.presentation.profile

import com.qbra.playlist.domain.GameLog

data class ProfileState(
    val isLoading: Boolean = false,
    val logs: List<GameLog> = emptyList(),
    val error: String = "",
    val username: String = ""
)

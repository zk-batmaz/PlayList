package com.qbra.playlist.presentation

import com.qbra.playlist.domain.GameDetail
import com.qbra.playlist.domain.GameLog
import com.qbra.playlist.domain.GameStat

data class GameDetailState(
    val isLoading: Boolean = false,
    val game: GameDetail? = null,
    val error: String = "",
    val userLog: GameLog? = null,
    val gameStat: GameStat? = null,
    val isLogSaving: Boolean = false // Kaydetme butonuna basıldığında dönen animasyon için
)

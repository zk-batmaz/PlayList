package com.qbra.playlist.domain

data class GameStat(
    val gameId: Int,
    val averageRating: Double = 0.0,
    val totalRatings: Int = 0 // Örn: 4.5 ortalama 12 değerlendirme
)

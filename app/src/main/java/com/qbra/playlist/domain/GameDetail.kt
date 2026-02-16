package com.qbra.playlist.domain

data class GameDetail(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rating: Double,
    val releaseDate: String
)

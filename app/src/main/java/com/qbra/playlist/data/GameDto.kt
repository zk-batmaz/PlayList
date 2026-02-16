package com.qbra.playlist.data
import com.google.gson.annotations.SerializedName
import com.qbra.playlist.domain.Game

data class GameDto(
    val id: Int,
    val name: String,
    @SerializedName("background_image")
    val backgroundImage: String?,
    val rating: Double
) {
    fun toDomain(): Game {
        return Game(
            id = id,
            name = name,
            imageUrl = backgroundImage ?: "",
            rating = rating
        )
    }
}
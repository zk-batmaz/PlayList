package com.qbra.playlist.data

import com.google.gson.annotations.SerializedName
import com.qbra.playlist.domain.GameDetail

data class GameDetailDto(
    val id: Int,
    val name: String,
    @SerializedName("description_raw")
    val description: String?,
    @SerializedName("background_image")
    val backgroundImage: String?,
    val rating: Double,
    val released: String?
) {
    fun toDomain(): GameDetail {
        return GameDetail(
            id = id,
            name = name,
            description = description ?: "Açıklama bulunamadı.",
            imageUrl = backgroundImage ?: "",
            rating = rating,
            releaseDate = released ?: "Bilinmiyor"
        )
    }
}

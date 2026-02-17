package com.qbra.playlist.domain

data class GameLog(
    val id: String = "",           // Firestore ID'si
    val userId: String = "",            // Oyunu kaydeden kullanıcının ID'si
    val gameId: Int = 0,               // RAWG'daki oyun ID'si
    val gameName: String = "",          // İleride profil sayfasında oyunu göstermek için adı
    val gameImageUrl: String = "",      // Profil sayfası için oyunun resmi
    val rating: Double? = null,    // Puan (Zorunlu değil, o yüzden nullable)
    val review: String? = null,    // Yorum (Zorunlu değil, o yüzden nullable)
    val timestamp: Long = System.currentTimeMillis() // Ne zaman kaydedildi?
)

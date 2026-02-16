package com.qbra.playlist.domain

interface GameRepository {
    // UI sadece bu fonksiyonu çağıracak ya başarılı bir oyun listesi ya da bir hata mesajı dönecek
    suspend fun getGames(searchQuery: String? = null, page: Int): Resource<List<Game>>
    suspend fun getGameDetail(id: Int): Resource<GameDetail>
}
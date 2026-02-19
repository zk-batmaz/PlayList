package com.qbra.playlist.data

import com.qbra.playlist.domain.Game
import com.qbra.playlist.domain.GameDetail
import com.qbra.playlist.domain.GameRepository
import com.qbra.playlist.domain.Resource

// Bu sınıf GameRepository arayüzünü uygular
class GameRepositoryImpl(
    private val api: GameApi
) : GameRepository {

    override suspend fun getGames(searchQuery: String?, page: Int): Resource<List<Game>> {
        return try {
            val response = api.getGames(
                apiKey = "c2d7f4f17e944cc38286e17511ed477a",
                searchQuery = searchQuery,
                page = page
            )

            val games = response.results.map { it.toDomain() }
            Resource.Success(games)

        } catch (e: Exception) {
            Resource.Error(message = "Bir hata oluştu: ${e.localizedMessage}")
        }
    }
    override suspend fun getGameDetail(id: Int): Resource<GameDetail> {
        return try {
            val response = api.getGameDetail(
                id = id,
                apiKey = "c2d7f4f17e944cc38286e17511ed477a"
            )
            Resource.Success(response.toDomain())
        } catch (e: Exception) {
            Resource.Error(message = "Detaylar alınamadı: ${e.localizedMessage}")
        }
    }
}
package com.qbra.playlist.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GameApi {
    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("search") searchQuery: String? = null,
        @Query("page") page: Int
    ): GameResponse
    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") id: Int,
        @Query("key") apiKey: String
    ): GameDetailDto
}
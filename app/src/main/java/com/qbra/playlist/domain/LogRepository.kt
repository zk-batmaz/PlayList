package com.qbra.playlist.domain

interface LogRepository {
    suspend fun saveGameLog(log: GameLog): Resource<Unit>

    suspend fun getGameStats(gameId: Int): Resource<GameStat>

    suspend fun getUserLogForGame(userId: String, gameId: Int): Resource<GameLog?>

    suspend fun getUserLogs(userId: String): Resource<List<GameLog>>

    suspend fun getLogsForGame(gameId: Int): Resource<List<GameLog>>
}
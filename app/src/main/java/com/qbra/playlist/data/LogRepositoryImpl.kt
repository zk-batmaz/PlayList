package com.qbra.playlist.data

import com.qbra.playlist.domain.GameLog
import com.qbra.playlist.domain.GameStat
import com.qbra.playlist.domain.LogRepository
import com.qbra.playlist.domain.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class LogRepositoryImpl(
    private val firestore: FirebaseFirestore
) : LogRepository {

    // OYUNU KAYDETME VE İSTATİSTİKLERİ GÜNCELLEME
    override suspend fun saveGameLog(log: GameLog): Resource<Unit> {
        return try {
            val documentId = "${log.userId}_${log.gameId}"
            firestore.collection("game_logs").document(documentId).set(log, SetOptions.merge()).await()

            calculateAndUpdateGameStats(log.gameId)

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Oyun kaydedilemedi.")
        }
    }

    // OYUNUN GENEL ORTALAMASINI GETİRME
    override suspend fun getGameStats(gameId: Int): Resource<GameStat> {
        return try {
            val document = firestore.collection("game_stats").document(gameId.toString()).get().await()
            if (document.exists()) {
                val average = document.getDouble("averageRating") ?: 0.0
                val total = document.getLong("totalRatings")?.toInt() ?: 0
                Resource.Success(GameStat(gameId, average, total))
            } else {
                // eğer oyun daha önce hiç puanlanmamışsa sıfır
                Resource.Success(GameStat(gameId, 0.0, 0))
            }
        } catch (e: Exception) {
            Resource.Error("İstatistikler alınamadı.")
        }
    }

    // KULLANICININ KENDİ KAYDINI GETİRME
    override suspend fun getUserLogForGame(userId: String, gameId: Int): Resource<GameLog?> {
        return try {
            val documentId = "${userId}_${gameId}"
            val document = firestore.collection("game_logs").document(documentId).get().await()

            if (document.exists()) {
                val log = document.toObject(GameLog::class.java)
                Resource.Success(log)
            } else {
                Resource.Success(null) // Kullanıcı bu oyunu henüz oynamamış
            }
        } catch (e: Exception) {
            Resource.Error("Kullanıcı kaydı alınamadı.")
        }
    }

    private suspend fun calculateAndUpdateGameStats(gameId: Int) {
        try {
            val querySnapshot = firestore.collection("game_logs")
                .whereEqualTo("gameId", gameId)
                .get()
                .await()

            var totalScore = 0.0
            var count = 0

            for (doc in querySnapshot.documents) {
                val rating = doc.getDouble("rating")
                if (rating != null) {
                    totalScore += rating
                    count++
                }
            }

            val average = if (count > 0) totalScore / count else 0.0
            val roundedAverage = Math.round(average * 10.0) / 10.0

            val newStat = GameStat(gameId, roundedAverage, count)
            firestore.collection("game_stats").document(gameId.toString()).set(newStat).await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // KULLANICININ TÜM KAYITLARINI GETİRME
    override suspend fun getUserLogs(userId: String): Resource<List<GameLog>> {
        return try {
            // Sadece bu kullanıcıya ait olan logları filtreleyerek çek
            val querySnapshot = firestore.collection("game_logs")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // Mapping
            val logs = querySnapshot.documents.mapNotNull { document ->
                document.toObject(GameLog::class.java)
            }

            // En son eklediği oyun en üstte görünsün diye zamana göre tersten sırala
            val sortedLogs = logs.sortedByDescending { it.timestamp }

            Resource.Success(sortedLogs)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Oyun geçmişi yüklenemedi.")
        }
    }

    override suspend fun getLogsForGame(gameId: Int): Resource<List<GameLog>> {
        return try {
            val querySnapshot = firestore.collection("game_logs")
                .whereEqualTo("gameId", gameId)
                .get()
                .await()

            val logs = querySnapshot.documents.mapNotNull { it.toObject(GameLog::class.java) }
            val sortedLogs = logs.sortedByDescending { it.timestamp }

            Resource.Success(sortedLogs)
        } catch (e: Exception) {
            Resource.Error("Topluluk yorumları getirilemedi.")
        }
    }
}
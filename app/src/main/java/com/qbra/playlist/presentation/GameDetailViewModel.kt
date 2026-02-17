package com.qbra.playlist.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qbra.playlist.domain.AuthRepository
import com.qbra.playlist.domain.GameDetail
import com.qbra.playlist.domain.GameLog
import com.qbra.playlist.domain.GameRepository
import com.qbra.playlist.domain.LogRepository
import com.qbra.playlist.domain.Resource
import kotlinx.coroutines.launch

class GameDetailViewModel(
    private val gameRepository: GameRepository,
    private val logRepository: LogRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(GameDetailState())
    val state: State<GameDetailState> = _state

    fun getGameDetail(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // RAWG API'den oyunu çek
            val gameResult = gameRepository.getGameDetail(id)

            // Firestore'dan oyunun genel istatistiklerini çek
            val statResult = logRepository.getGameStats(id)

            // Kullanıcı giriş yapmışsa, bu oyuna ait kendi kaydını çek
            val currentUser = authRepository.getCurrentUser()
            var userLogResult: GameLog? = null

            if (currentUser != null) {
                val logRes = logRepository.getUserLogForGame(currentUser.uid, id)
                if (logRes is Resource.Success) {
                    userLogResult = logRes.data
                }
            }

            // Verileri Ekrana gönder
            when (gameResult) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        game = gameResult.data,
                        gameStat = statResult.data,
                        userLog = userLogResult
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = gameResult.message ?: "Detaylar yüklenemedi."
                    )
                }
                is Resource.Loading -> { }
            }
        }
    }

    fun saveGameLog(rating: Double?, review: String?) {
        val game = _state.value.game ?: return
        val currentUser = authRepository.getCurrentUser() ?: return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLogSaving = true)

            val log = GameLog(
                userId = currentUser.uid,
                gameId = game.id,
                gameName = game.name,
                gameImageUrl = game.imageUrl,
                rating = rating,
                review = if (review.isNullOrBlank()) null else review
            )

            when (val result = logRepository.saveGameLog(log)) {
                is Resource.Success -> {
                    // kayıt başarılı olunca güncel istatistikleri ve logu görmek için sayfayı yenile
                    getGameDetail(game.id)
                    _state.value = _state.value.copy(isLogSaving = false)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLogSaving = false,
                        error = result.message ?: "Kaydedilemedi."
                    )
                }
                is Resource.Loading -> { }
            }
        }
    }
}
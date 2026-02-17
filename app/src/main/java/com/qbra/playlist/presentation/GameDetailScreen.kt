package com.qbra.playlist.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlin.math.roundToInt

@Composable
fun GameDetailScreen(
    viewModel: GameDetailViewModel,
    gameId: Int
) {
    val state = viewModel.state.value

    LaunchedEffect(key1 = gameId) {
        viewModel.getGameDetail(gameId)
    }

    var showLogDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.error.isNotBlank()) {
            Text(text = state.error, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
        } else {
            state.game?.let { game ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Oyun Resmi
                    AsyncImage(
                        model = game.imageUrl,
                        contentDescription = game.name,
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {

                        // Başlık ve Logla Butonu
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = game.name,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = { showLogDialog = true },
                                enabled = !state.isLogSaving
                            ) {
                                if (state.isLogSaving) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                                } else {
                                    // Eğer veritabanından kullanıcı kaydı geldiyse buton adını Düzenle yap
                                    Text(if (state.userLog != null) "Kaydımı Düzenle" else "Oynadım / Logla")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // İstatistik Kartı (RAWG ve PlayList)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // RAWG Puanı
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("RAWG Puanı", style = MaterialTheme.typography.labelLarge)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
                                        Text("${game.rating} / 5.0", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // PlayList (Uygulama İçi) Puanı
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("PlayList Puanı", style = MaterialTheme.typography.labelLarge)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF9800))
                                        val playListRating = state.gameStat?.averageRating ?: 0.0
                                        val totalRatings = state.gameStat?.totalRatings ?: 0
                                        Text(
                                            text = if (totalRatings > 0) "$playListRating / 5.0" else "Puan Yok",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if ((state.gameStat?.totalRatings ?: 0) > 0) {
                                        Text("(${state.gameStat?.totalRatings} değerlendirme)", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Kullanıcının Kendi Kaydı Varsa Ekranda Gösterme
                        state.userLog?.let { log ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Senin Kaydın", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                                    if (log.rating != null) {
                                        Text("Verdiğin Puan: ${log.rating} / 5.0")
                                    } else {
                                        Text("Sadece oynadığını belirttin, puan vermedin.")
                                    }
                                    if (!log.review.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Yorumun: \"${log.review}\"", fontStyle = FontStyle.Italic)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Oyun Hikayesi
                        Text("Hikaye", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = game.description)
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        // LOGLAMA PENCERESİ (Dialog)
        if (showLogDialog) {
            LogGameDialog(
                initialRating = state.userLog?.rating,
                initialReview = state.userLog?.review,
                onDismiss = { showLogDialog = false },
                onSave = { rating, review ->
                    viewModel.saveGameLog(rating, review)
                    showLogDialog = false
                }
            )
        }
    }
}

@Composable
fun LogGameDialog(
    initialRating: Double?,
    initialReview: String?,
    onDismiss: () -> Unit,
    onSave: (Double?, String?) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(initialRating?.toFloat() ?: 0f) }
    var reviewText by remember { mutableStateOf(initialReview ?: "") }

    val roundedPosition = (sliderPosition * 10f).roundToInt() / 10f

    val isSaveEnabled = if (reviewText.isNotBlank()) roundedPosition > 0f else true

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Oyunu Logla") },
        text = {
            Column {
                Text("Bu oyuna puan ver (İsteğe bağlı):")

                // Dinamik Puan Göstergesi
                Text(
                    text = if (roundedPosition > 0f) "$roundedPosition / 5.0" else "Puan Vermek İstemiyorum",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp),
                    color = if (roundedPosition > 0f) MaterialTheme.colorScheme.primary else Color.Gray
                )

                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 0f..5f,
                    steps = 9
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Yorumun (İsteğe bağlı)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                if (reviewText.isNotBlank() && roundedPosition == 0f) {
                    Text(
                        text = "* Yorum yazıyorsan puan vermek zorundasın.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalRating = if (roundedPosition > 0f) roundedPosition.toDouble() else null
                    onSave(finalRating, reviewText)
                },
                enabled = isSaveEnabled // Kurallara uymazsa Kaydet butonu inaktif olur
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}
package com.qbra.playlist.presentation.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    userId: String? = null,
    onNavigateBack: () -> Unit,
    onGameClick: (Int) -> Unit
) {
    val state = viewModel.state.value
    var selectedLogForDialog by remember { mutableStateOf<com.qbra.playlist.domain.GameLog?>(null) }

    LaunchedEffect(key1 = userId) {
        viewModel.loadProfile(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.username.isNotBlank()) "${state.username}'in Profili" else "Profilim") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error.isNotBlank()) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            } else if (state.logs.isEmpty()) {
                // Empty State
                Text(
                    text = "Henüz hiçbir oyunu loglamadın.\nKeşfetmeye başla!",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
            } else {
                // Oynadığı oyunların listesi
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.logs) { log ->
                        Card(
                            onClick = { onGameClick(log.gameId) },
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Oyun Resmi
                                AsyncImage(
                                    model = log.gameImageUrl,
                                    contentDescription = log.gameName,
                                    modifier = Modifier.size(100.dp),
                                    contentScale = ContentScale.Crop
                                )

                                // Oyun Bilgileri, Puan ve Yorum
                                Column(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .weight(1f)
                                ) {
                                    Text(text = log.gameName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))

                                    if (log.rating != null) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "${log.rating} / 5.0", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Text(text = "Sadece Oynandı", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                    }

                                    if (!log.review.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "\"${log.review}\"",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontStyle = FontStyle.Italic,
                                            maxLines = 2 // Yorum çok uzunsa kartı bozmasın diye 2 satırla sınırla
                                        )
                                    }

                                    selectedLogForDialog?.let { log ->
                                        AlertDialog(
                                            onDismissRequest = { selectedLogForDialog = null }, // Dışarı tıklayınca kapat
                                            title = { Text(text = log.gameName, fontWeight = FontWeight.Bold) },
                                            text = {
                                                Column {
                                                    // Oyun Resmi (Tıklanabilir)
                                                    AsyncImage(
                                                        model = log.gameImageUrl,
                                                        contentDescription = log.gameName,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(180.dp)
                                                            .clickable { // Resme tıklayınca dialogu kapat ve oyun detayına git!
                                                                selectedLogForDialog = null
                                                                onGameClick(log.gameId)
                                                            },
                                                        contentScale = ContentScale.Crop
                                                    )

                                                    Spacer(modifier = Modifier.height(16.dp))

                                                    if (log.rating != null) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
                                                            Text(text = " Verdiği Puan: ${log.rating} / 5.0", fontWeight = FontWeight.Bold)
                                                        }
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                    }

                                                    // Tam yorumu gösteren ve kaydırılabilir alan
                                                    Text(
                                                        text = if (!log.review.isNullOrBlank()) "\"${log.review}\"" else "Bu oyun için yorum yazılmamış.",
                                                        fontStyle = FontStyle.Italic,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(text = "* Detaylara gitmek için görsele tıklayın.", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                                }
                                            },
                                            confirmButton = {
                                                Button(onClick = { selectedLogForDialog = null }) {
                                                    Text("Kapat")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
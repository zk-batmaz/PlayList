package com.qbra.playlist.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun GameDetailScreen(
    viewModel: GameDetailViewModel,
    gameId: Int // Önceki ekrandan gelen id
) {
    LaunchedEffect(key1 = gameId) {
        viewModel.getGameDetail(gameId)
    }

    val state = viewModel.state.value

    Box(modifier = Modifier.fillMaxSize()) {
        state.game?.let { game ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Kapak Resmi
                AsyncImage(
                    model = game.imageUrl,
                    contentDescription = game.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Oyun Bilgileri
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = game.name,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Çıkış Tarihi: ${game.releaseDate} | Puan: ${game.rating}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = game.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (state.error.isNotBlank()) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
package com.qbra.playlist.presentation


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.qbra.playlist.domain.Game
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search

@Composable
fun GameItem(
    game: Game,
    onItemClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick(game.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coil kütüphanesi internetteki resmi asenkron olarak indirip gösterir
            AsyncImage(
                model = game.imageUrl,
                contentDescription = game.name,
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Puan: ${game.rating}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun GameListScreen(
    viewModel: GameViewModel,
    onGameClick: (Int) -> Unit
) {
    val state = viewModel.state.value
    val searchQuery = viewModel.searchQuery.value

    Column(modifier = Modifier.fillMaxSize()) {

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Oyun Ara (Örn: GTA, Mario)...") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { viewModel.searchGames() }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Ara"
                    )
                }
            }
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (state.games.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.games.size) { index ->
                        val game = state.games[index]

                        if (index >= state.games.size - 1 && !state.isLoading) {
                            viewModel.getGames(viewModel.searchQuery.value)
                        }

                        GameItem(
                            game = game,
                            onItemClick = { id -> onGameClick(id) }
                        )
                    }
                }
            }

            if (state.error.isNotBlank()) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    viewModel: GameViewModel,
    onGameClick: (Int) -> Unit,
    onLogoutClick: () -> Unit,
    onProfileClick: () -> Unit,
    onUserClick: (String) -> Unit
) {
    val state = viewModel.state.value
    val searchQuery = viewModel.searchQuery.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PlayList") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profil")
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Çıkış Yap")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ARAMA ÇUBUĞU
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Oyun veya Kullanıcı Ara...") },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { viewModel.performSearch() }) { // DÜZELTME: Artık yönlendiriciyi çağırıyor
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Ara")
                    }
                }
            )

            // SEKME TASARIMI (Oyunlar / Kullanıcılar)
            TabRow(
                selectedTabIndex = if (state.isSearchModeUsers) 1 else 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = !state.isSearchModeUsers,
                    onClick = { viewModel.toggleSearchMode(false) },
                    text = { Text("Oyunlar") }
                )
                Tab(
                    selected = state.isSearchModeUsers,
                    onClick = { viewModel.toggleSearchMode(true) },
                    text = { Text("Kullanıcılar") }
                )
            }

            // DİNAMİK LİSTE KISMI
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {

                if (state.isSearchModeUsers) {
                    // KULLANICI LİSTESİ
                    if (state.isUserLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (state.users.isEmpty() && searchQuery.isNotBlank()) {
                        Text("Kullanıcı bulunamadı.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                            items(state.users.size) { index ->
                                val user = state.users[index]
                                Card(
                                    onClick = { onUserClick(user.uid) }, // Kullanıcıya tıklama eylemi
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(text = user.username, style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // OYUN LİSTESİ
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
                }

                if (state.error.isNotBlank() && !state.isSearchModeUsers) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }

                if (state.isLoading && !state.isSearchModeUsers) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

package com.qbra.playlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.qbra.playlist.data.GameApi
import com.qbra.playlist.data.GameRepositoryImpl
import com.qbra.playlist.presentation.GameDetailScreen
import com.qbra.playlist.presentation.GameDetailViewModel
import com.qbra.playlist.presentation.GameListScreen
import com.qbra.playlist.presentation.GameViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.rawg.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(GameApi::class.java)
        val repository = GameRepositoryImpl(api)

        // ViewModel Factory
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                    return GameViewModel(repository) as T
                }
                if (modelClass.isAssignableFrom(GameDetailViewModel::class.java)) {
                    return GameDetailViewModel(repository) as T
                }
                throw IllegalArgumentException("Bilinmeyen ViewModel sınıfı")
            }
        }

        //UI ve Navigation
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "game_list") {

                composable("game_list") {
                    val viewModel: GameViewModel = viewModel(factory = factory)
                    GameListScreen(
                        viewModel = viewModel,
                        onGameClick = { gameId ->
                            // kullanıcı bir oyuna tıkladığında oyunun id'si detay ekranına yollanıyor
                            navController.navigate("game_detail/$gameId")
                        }
                    )
                }

                composable(
                    route = "game_detail/{gameId}",
                    arguments = listOf(navArgument("gameId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val gameId = backStackEntry.arguments?.getInt("gameId") ?: return@composable
                    val detailViewModel: GameDetailViewModel = viewModel(factory = factory)
                    GameDetailScreen(
                        viewModel = detailViewModel,
                        gameId = gameId
                    )
                }
            }
        }
    }
}
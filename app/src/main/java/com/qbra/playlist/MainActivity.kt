package com.qbra.playlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.qbra.playlist.data.AuthRepositoryImpl
import com.qbra.playlist.data.GameApi
import com.qbra.playlist.data.GameRepositoryImpl
import com.qbra.playlist.presentation.GameDetailScreen
import com.qbra.playlist.presentation.GameDetailViewModel
import com.qbra.playlist.presentation.GameListScreen
import com.qbra.playlist.presentation.GameViewModel
import com.qbra.playlist.presentation.auth.AuthViewModel
import com.qbra.playlist.presentation.auth.LoginScreen
import com.qbra.playlist.presentation.auth.RegisterScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qbra.playlist.data.LogRepositoryImpl
import com.qbra.playlist.presentation.profile.ProfileScreen
import com.qbra.playlist.presentation.profile.ProfileViewModel
import com.qbra.playlist.presentation.splash.SplashScreen
import com.qbra.playlist.ui.theme.PlayListTheme // TEMANI İÇERİ AKTARDIK
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // API VE REPOSITORY KURULUMU
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.rawg.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(GameApi::class.java)
        val gameRepository = GameRepositoryImpl(api)

        // FIREBASE VE AUTH REPOSITORY KURULUMU
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val authRepository = AuthRepositoryImpl(auth, firestore)
        val logRepository = LogRepositoryImpl(firestore)

        // VIEWMODEL FACTORY
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                    return GameViewModel(gameRepository, authRepository) as T
                }
                if (modelClass.isAssignableFrom(GameDetailViewModel::class.java)) {
                    return GameDetailViewModel(gameRepository, logRepository, authRepository) as T
                }
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    return AuthViewModel(authRepository) as T
                }
                if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                    return ProfileViewModel(authRepository, logRepository) as T
                }
                throw IllegalArgumentException("Bilinmeyen ViewModel sınıfı")
            }
        }

        setContent {
            PlayListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // AUTH ROUTING
                    val currentUser = authRepository.getCurrentUser()
                    val startDestination = if (currentUser != null) "game_list" else "login"

                    NavHost(navController = navController, startDestination = "splash") {

                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    val currentUser = authRepository.getCurrentUser()
                                    if (currentUser != null) {
                                        // Giriş yapmışsa oyunlara git ve splash'i geçmişten sil
                                        navController.navigate("game_list") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    } else {
                                        // Giriş yapmamışsa logine git ve splash'i geçmişten sil
                                        navController.navigate("login") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        composable("login") {
                            val viewModel: AuthViewModel = viewModel(factory = factory)
                            LoginScreen(
                                viewModel = viewModel,
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = {
                                    navController.navigate("game_list") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("register") {
                            val viewModel: AuthViewModel = viewModel(factory = factory)
                            RegisterScreen(
                                viewModel = viewModel,
                                onNavigateToLogin = { navController.navigate("login") },
                                onRegisterSuccess = {
                                    navController.navigate("game_list") {
                                        popUpTo("register") { inclusive = true }
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // OYUN ROTALARI

                        composable("game_list") {
                            val gameViewModel: GameViewModel = viewModel(factory = factory)
                            val authViewModel: AuthViewModel = viewModel(factory = factory)

                            GameListScreen(
                                viewModel = gameViewModel,
                                onGameClick = { gameId ->
                                    navController.navigate("game_detail/$gameId")
                                },
                                onLogoutClick = {
                                    authViewModel.signOut()
                                    navController.navigate("login") {
                                        popUpTo("game_list") { inclusive = true }
                                    }
                                },
                                onProfileClick = {
                                    navController.navigate("profile")
                                },
                                onUserClick = { clickedUserId ->
                                    navController.navigate("profile?userId=$clickedUserId")
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

                        composable(
                            route = "profile?userId={userId}",
                            arguments = listOf(navArgument("userId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) { backStackEntry ->
                            val targetUserId = backStackEntry.arguments?.getString("userId")
                            val viewModel: ProfileViewModel = viewModel(factory = factory)

                            ProfileScreen(
                                viewModel = viewModel,
                                userId = targetUserId,
                                onNavigateBack = { navController.popBackStack() },
                                onGameClick = { gameId ->
                                    navController.navigate("game_detail/$gameId")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
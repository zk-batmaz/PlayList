package com.qbra.playlist.presentation.auth

import com.qbra.playlist.domain.User

data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String = "",
    val isSuccess: Boolean = false // Navigation yapabilmek için flag
)

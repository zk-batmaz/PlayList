package com.qbra.playlist.domain

interface AuthRepository {
    suspend fun signUp(email: String, password: String, username: String): Resource<User>

    suspend fun signIn(email: String, password: String): Resource<User>

    fun getCurrentUser(): User?

    suspend fun signOut()

    suspend fun searchUsers(query: String): Resource<List<User>>

    suspend fun getUserById(userId: String): Resource<User>
}
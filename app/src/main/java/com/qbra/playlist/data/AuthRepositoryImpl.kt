package com.qbra.playlist.data

import com.qbra.playlist.domain.AuthRepository
import com.qbra.playlist.domain.Resource
import com.qbra.playlist.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun signUp(email: String, password: String, username: String): Resource<User> {
        return try {
            // Önce Firebase Authentication'a E-posta ve Şifre ile kullanıcıyı kaydet
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // Kayıt başarılıysa kendi User modelimizi oluştur
                val user = User(
                    uid = firebaseUser.uid,
                    username = username,
                    email = email
                )

                firestore.collection("users").document(user.uid).set(user).await()

                Resource.Success(user)
            } else {
                Resource.Error("Kullanıcı oluşturulamadı.")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Bilinmeyen bir hata oluştu.")
        }
    }

    override suspend fun signIn(email: String, password: String): Resource<User> {
        return try {
            // Firebase Auth ile giriş yap
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // Giriş başarılıysa veritabanına gidip bu kullanıcının username bilgisini çek
                val document = firestore.collection("users").document(firebaseUser.uid).get().await()
                val username = document.getString("username") ?: ""

                val user = User(
                    uid = firebaseUser.uid,
                    username = username,
                    email = email
                )
                Resource.Success(user)
            } else {
                Resource.Error("Giriş başarısız.")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Hatalı e-posta veya şifre.")
        }
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            User(uid = firebaseUser.uid, username = "", email = firebaseUser.email ?: "")
        } else {
            null
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun searchUsers(query: String): Resource<List<User>> {
        return try {
            if (query.isBlank()) return Resource.Success(emptyList())

            val snapshot = firestore.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()

            val users = snapshot.documents.mapNotNull { doc ->
                val uid = doc.id
                val username = doc.getString("username") ?: ""
                val email = doc.getString("email") ?: ""

                // DÜZELTME: İsimlendirilmiş argümanlar kullanarak eşleşmeyi garantiye alıyoruz
                com.qbra.playlist.domain.User(
                    uid = uid,
                    username = username,
                    email = email
                )
            }

            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Kullanıcılar bulunamadı.")
        }
    }

    override suspend fun getUserById(userId: String): Resource<User> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                val uid = document.id
                val username = document.getString("username") ?: ""
                val email = document.getString("email") ?: ""

                Resource.Success(
                    com.qbra.playlist.domain.User(uid = uid, username = username, email = email)
                )
            } else {
                Resource.Error("Kullanıcı bulunamadı.")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Bir hata oluştu.")
        }
    }
}
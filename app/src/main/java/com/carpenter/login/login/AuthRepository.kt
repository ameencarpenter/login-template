package com.carpenter.login.login

import com.carpenter.login.utils.await
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class AuthRepository {

    suspend fun loginWithGoogle(idToken: String): Unit = withContext(IO) {
        Firebase.auth
            .signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .await()
    }

    suspend fun signUpWithEmail(email: String, password: String): Unit = withContext(IO) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun signInWithEmail(email: String, password: String): Unit = withContext(IO) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun sendPasswordResetEmail(email: String): Unit = withContext(IO) {
        Firebase.auth.sendPasswordResetEmail(email).await()
    }

    suspend fun signInWithPhoneNumber(credential: AuthCredential): Unit = withContext(IO) {
        Firebase.auth.signInWithCredential(credential).await()
    }

    companion object {
        @Volatile
        var INSTANCE: AuthRepository? = null

        fun getInstance() = INSTANCE ?: synchronized(this) {
            val instance = AuthRepository()
            INSTANCE = instance
            return instance
        }
    }
}
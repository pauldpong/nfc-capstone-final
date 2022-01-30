package com.capstone.nfc.data

import com.capstone.nfc.Constants.DEFAULT_ERROR_MESSAGE
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.capstone.nfc.data.Response.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    val userAuthenticated get() = auth.currentUser != null

    fun signInAnon() = flow {
        try {
            emit(Loading)
            val user = auth.signInAnonymously().await().user
            emit(Success(user))
        } catch (e: Exception) {
            emit(Failure(e.message ?: DEFAULT_ERROR_MESSAGE))
        }
    }

    fun signOut() = flow {
        try {
            emit(Loading)
            auth.signOut()
        } catch (e: Exception) {
            emit(Failure(e.message ?: DEFAULT_ERROR_MESSAGE))
        }
    }

    @ExperimentalCoroutinesApi
    fun getAuthState() = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            offer(auth.currentUser == null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }
}
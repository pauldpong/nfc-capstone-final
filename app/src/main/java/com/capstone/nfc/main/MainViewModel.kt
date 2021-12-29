package com.capstone.nfc.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.nfc.data.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    fun signOut() = liveData(Dispatchers.IO) {
        authRepository.signOut().collect { response -> emit(response) }
    }

    fun getAuthState() = liveData(Dispatchers.IO) {
        authRepository.getAuthState().collect { response ->
            emit(response)
        }
    }
}
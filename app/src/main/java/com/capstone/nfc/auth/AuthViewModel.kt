package com.capstone.nfc.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.nfc.data.AuthRepository
import com.capstone.nfc.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
): ViewModel() {
    fun signInAnon() = liveData(Dispatchers.IO) {
        authRepository.signInAnon().collect { response -> emit(response) }
    }

    fun createUser() = liveData(Dispatchers.IO) {
        userRepository.createUser().collect { response -> emit(response) }
    }
}
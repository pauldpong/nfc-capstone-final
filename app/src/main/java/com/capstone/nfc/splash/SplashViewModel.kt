package com.capstone.nfc.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.nfc.data.AuthRepository
import com.capstone.nfc.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val userRepository: UserRepository
): ViewModel() {
    val userAuthenticated = authRepository.userAuthenticated

    fun createUser() = liveData(Dispatchers.IO) {
        userRepository.createUser().collect { response -> emit(response) }
    }

    fun createUser(email: String?) = liveData(Dispatchers.IO) {
        userRepository.createUser(email).collect { response -> emit(response) }
    }
}
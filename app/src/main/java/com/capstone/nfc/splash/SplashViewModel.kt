package com.capstone.nfc.splash

import androidx.lifecycle.ViewModel
import com.capstone.nfc.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepository: AuthRepository
): ViewModel() {
    val userAuthenticated = authRepository.userAuthenticated
}
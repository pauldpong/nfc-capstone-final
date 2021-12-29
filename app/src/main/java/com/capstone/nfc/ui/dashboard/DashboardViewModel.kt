package com.capstone.nfc.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.nfc.data.AuthRepository
import com.capstone.nfc.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: UserRepository
): ViewModel() {
    fun getUser() = liveData(Dispatchers.IO) {
        repository.getUser().collect { response ->
            emit(response)
        }
    }

    fun signOut() = liveData(Dispatchers.IO) {
        authRepository.signOut().collect { response -> emit(response) }
    }
}
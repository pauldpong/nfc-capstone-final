package com.capstone.nfc.ui.dashboard

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.nfc.data.AuthRepository
import com.capstone.nfc.data.FileRepository
import com.capstone.nfc.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: UserRepository,
    private val fileRepository: FileRepository
): ViewModel() {
    fun getUser() = liveData(Dispatchers.IO) {
        repository.getUser().collect { response ->
            emit(response)
        }
    }

    fun uploadPdf(uri: Uri) = liveData(Dispatchers.IO) {
        fileRepository.uploadFile(uri).collect { response ->
            emit(response)
        }
    }

    fun getFiles() = liveData(Dispatchers.IO) {
        fileRepository.getFiles().collect { response ->
            emit(response)
        }
    }

    fun signOut() = liveData(Dispatchers.IO) {
        authRepository.signOut().collect { response -> emit(response) }
    }
}
package com.capstone.nfc.ui.dashboard

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.capstone.nfc.data.*
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: UserRepository,
    private val fileRepository: FileRepository
): ViewModel() {
    private val myFiles: MutableLiveData<List<StorageFile>> by lazy {
        MutableLiveData<List<StorageFile>>().also {
            Log.e("dash", "reloading")
            loadMyFiles()
        }
    }
    fun getUser() = liveData(Dispatchers.IO) {
        repository.getUser().collect { response ->
            emit(response)
        }
    }

    fun getMyFiles(): LiveData<List<StorageFile>> = myFiles

    fun uploadPdf(uri: Uri, fullFileName: String) = liveData(Dispatchers.IO) {
        fileRepository.uploadFile(uri, fullFileName).collect { response ->
            emit(response)
        }
    }

    fun loadMyFiles() = viewModelScope.launch {
        fileRepository.files.collect { files ->
            myFiles.value = files
        }
    }

    fun signOut() = liveData(Dispatchers.IO) {
        authRepository.signOut().collect { response -> emit(response) }
    }
}
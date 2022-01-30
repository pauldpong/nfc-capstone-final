package com.capstone.nfc.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nfc.data.FileMetadata
import com.capstone.nfc.data.StorageFile
import com.capstone.nfc.data.UserRepository
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val sharedFiles: MutableLiveData<List<FileMetadata>> by lazy {
        MutableLiveData<List<FileMetadata>>().also {
            loadSharedFiles()
        }
    }

    fun getSharedFiles(): LiveData<List<FileMetadata>> = sharedFiles

    private fun loadSharedFiles() = viewModelScope.launch {
        userRepository.getSharedFiles().map { references ->
            references.map { it.get().await().toObject<FileMetadata>()!! }
        }.collect {
            sharedFiles.value = it
        }
    }
}
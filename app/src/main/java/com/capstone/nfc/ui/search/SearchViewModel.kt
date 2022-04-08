package com.capstone.nfc.ui.search

import androidx.lifecycle.*
import com.capstone.nfc.data.FileRepository
import com.capstone.nfc.data.StorageFile
import com.capstone.nfc.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository
): ViewModel() {
    val sharedFiles = liveData {
        userRepository.getSharedFiles().collect {
            it.collect { response ->
                emit(response)
            }
        }
    }

    private val myFiles: MutableLiveData<List<StorageFile>> by lazy {
        MutableLiveData<List<StorageFile>>().also {
            loadMyFiles()
        }
    }

    fun getUser() = liveData(Dispatchers.IO) {
        userRepository.getUser().collect { response ->
            emit(response)
        }
    }

    fun getMyFiles(): LiveData<List<StorageFile>> = myFiles

    fun loadMyFiles() = viewModelScope.launch {
        fileRepository.files.collect { files ->
            myFiles.value = files
        }
    }
}
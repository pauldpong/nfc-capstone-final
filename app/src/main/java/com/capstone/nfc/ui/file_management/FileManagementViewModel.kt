package com.capstone.nfc.ui.file_management

import android.util.Log
import androidx.lifecycle.*
import com.capstone.nfc.data.FileMetadata
import com.capstone.nfc.data.FileRepository
import com.capstone.nfc.data.Response
import com.capstone.nfc.data.StorageFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FileManagementViewModel @Inject constructor(
    private val fileRepository: FileRepository
): ViewModel() {
    private val fileMetadata: MutableLiveData<Response<FileMetadata>> by lazy {
        MutableLiveData<Response<FileMetadata>>()
    }

    fun loadFileMetadata(filePath: String) = viewModelScope.launch {
        fileRepository.getFileMetadata(filePath).collect {
            fileMetadata.value = it
        }
    }

    fun getFileMetadata(): LiveData<Response<FileMetadata>> = fileMetadata

    fun revokeAccess(fileUUID: String, uid: String) = liveData {
        emit(Response.Loading)

        try {
            fileRepository.revokeAccess(fileUUID, uid).collect {
                emit(Response.Success(it))
            }
        } catch (e: Exception) {
            emit(Response.Failure())
        }
    }
}
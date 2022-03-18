package com.capstone.nfc.ui.file_management

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.nfc.data.FileRepository
import com.capstone.nfc.data.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FileManagementViewModel @Inject constructor(
    private val fileRepository: FileRepository
): ViewModel() {
    fun getFileMetadata(filePath: String) = liveData {
        fileRepository.getFileMetadata(filePath).collect {
            emit(it)
        }
    }

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
package com.capstone.nfc.ui.shared

import android.util.Log
import androidx.lifecycle.*
import com.capstone.nfc.data.FileMetadata
import com.capstone.nfc.data.Response
import com.capstone.nfc.data.StorageFile
import com.capstone.nfc.data.UserRepository
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    val sharedFiles = liveData {
        userRepository.getSharedFiles().collect {
            it.collect { response ->
                emit(response)
            }
        }
    }
}
package com.capstone.nfc.ui.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.nfc.data.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class FilePreviewViewModel @Inject constructor(
    private val fileRepository: FileRepository
): ViewModel() {
    fun getFile(filePath: String) = liveData(Dispatchers.IO) {
        fileRepository.getFile(filePath).collect {
            emit(it)
        }
    }
}

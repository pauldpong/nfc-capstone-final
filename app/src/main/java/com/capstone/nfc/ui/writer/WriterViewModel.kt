package com.capstone.nfc.ui.writer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.nfc.data.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class WriterViewModel @Inject constructor(
    private val fileRepository: FileRepository
): ViewModel() {
}
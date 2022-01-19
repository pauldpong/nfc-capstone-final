package com.capstone.nfc.ui.reader

import androidx.lifecycle.ViewModel
import com.capstone.nfc.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    val uid get() = userRepository.uid

    fun addShared(accessToken: String) {
        userRepository.addShared(accessToken)
    }
}
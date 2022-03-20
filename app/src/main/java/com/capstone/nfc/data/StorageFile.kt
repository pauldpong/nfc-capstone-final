package com.capstone.nfc.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StorageFile(
    val name: String,
    val type: String,
    val path: String,
    val downloadUrl: String,
    val uuid: String
) : Parcelable
package com.capstone.nfc.data

import android.net.Uri

data class StorageFile(
    val name: String,
    val type: String,
    val path: String,
    val downloadUrl: Uri,
    val uuid: String
)
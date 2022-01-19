package com.capstone.nfc.data

import com.google.firebase.storage.StorageReference

data class File(val name: String, val path: String) {
    companion object {
        fun from(reference: StorageReference): File {
            return File(name = reference.name, path = reference.path)
        }
    }
}
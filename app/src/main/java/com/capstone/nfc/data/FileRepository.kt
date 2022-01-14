package com.capstone.nfc.data

import android.net.Uri
import android.util.Log
import com.capstone.nfc.Constants
import com.capstone.nfc.data.Response.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    private val dataSource: FirebaseFileDataSource
) {
    val files: Flow<List<File>> =
        dataSource.getFiles()
            .map { references -> references.map { File.from(it) } }

    fun uploadFile(uri: Uri, fileName: String) = dataSource.uploadFile(uri, fileName)
}
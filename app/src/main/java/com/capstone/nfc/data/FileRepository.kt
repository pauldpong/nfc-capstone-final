package com.capstone.nfc.data

import android.net.Uri
import android.util.Log
import com.capstone.nfc.Constants
import com.capstone.nfc.Constants.FILES_REF
import com.capstone.nfc.data.Response.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val dataSource: FirebaseFileDataSource,
    @Named(FILES_REF) private val filesRef: CollectionReference

) {
    val files: Flow<List<StorageFile>> = dataSource.getFiles()

    fun uploadFile(uri: Uri, fullFileName: String) = dataSource.uploadFile(uri, fullFileName)

    fun getFile(filePath: String) = dataSource.getFile(filePath)

    fun addAccessor(fileUUID: String, requesterUid: String) {
        Log.e("test", fileUUID + " "  + requesterUid)
        filesRef.document(fileUUID).get().addOnSuccessListener { document ->
            if (document != null) {
                val metadata = document.toObject<FileMetadata>()
                metadata?.let {
                    it.accessors.add(requesterUid)
                    filesRef.document(fileUUID).update("accessors", metadata.accessors)
                }
            }
        }
    }
}
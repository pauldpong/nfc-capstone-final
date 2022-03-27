package com.capstone.nfc.data

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.capstone.nfc.Constants.FILES_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FirebaseFileDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    @Named(FILES_REF) private val filesRef: CollectionReference
) {
    fun getFiles(): Flow<List<StorageFile>> = flow {
        auth.currentUser?.apply {
            val userFiles = storage.reference.child(uid).listAll().await()
            val result = mutableListOf<StorageFile>()

            for (reference in userFiles.items) {
                val metadata: StorageMetadata = reference.metadata.await()
                val downloadUrl = reference.downloadUrl.await().toString()
                result.add(StorageFile(reference.name, metadata.contentType!!, reference.path, downloadUrl, metadata.getCustomMetadata("uuid")!!))
            }

            emit(result)
        }
    }

    fun uploadFile(uri: Uri, fullFileName: String) = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                val filePath = "$uid/$fullFileName"
                val reference = storage.reference.child(filePath)
                val uuid = UUID.randomUUID().toString()
                val metadata = storageMetadata {
                    setCustomMetadata("uuid", uuid)
                }
                val snapshot = reference.putFile(uri, metadata).await()

                val fileName = fullFileName.split('.')[0]
                var type: String? = null
                val extension = MimeTypeMap.getFileExtensionFromUrl(fullFileName)
                if (extension != null) {
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }

                val newFileMetadata = FileMetadata(fileName, type ?: "", filePath, uid, mutableListOf())
                filesRef.document(uuid).set(newFileMetadata).await()

                emit(Response.Success(snapshot.metadata))
            }
        } catch (e: Exception) {
            Log.e("datasource", e.message.toString())
            emit(Response.Failure())
        }
    }

    @ExperimentalCoroutinesApi
    fun getFile(filePath: String) = callbackFlow {
        storage.reference.child(filePath).downloadUrl
            .addOnSuccessListener {
                offer(Response.Success(it))
                close()
            }
            .addOnFailureListener {
                offer(Response.Failure())
                close(it)
            }
//                .addOnProgressListener {
//
//                }

        awaitClose()
    }
}
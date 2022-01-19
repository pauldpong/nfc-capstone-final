package com.capstone.nfc.data

import android.net.Uri
import android.util.Log
import com.capstone.nfc.Constants.FILES_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FirebaseFileDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    @Named(FILES_REF) private val filesRef: CollectionReference
) {
    fun getFiles(): Flow<List<StorageReference>> = flow {
        auth.currentUser?.apply {
            val reference = storage.reference.child(uid)
            val result = reference.listAll().await()
            emit(result.items)
        }
    }

    fun uploadFile(uri: Uri, fileName: String) = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                val fileId = "$uid/$fileName"
                val reference = storage.reference.child(fileId)
                val snapshot = reference.putFile(uri).await()

                val newFileMetadata = FileMetadata(uid, mutableListOf())
                filesRef.document(fileName).set(newFileMetadata).await()

                emit(Response.Success(snapshot.metadata))
            }
        } catch (e: Exception) {
            emit(Response.Failure())
        }
    }

    fun getFileType(filePath: String): Flow<String> = flow {
        auth.currentUser?.apply {
            storage.reference.child(filePath).metadata.await().contentType?.let { emit(it) }
        }
    }

    fun getFile(filePath: String): Flow<ByteArray> = flow {
        auth.currentUser?.apply {
            val bytes = storage.reference.child(filePath).getBytes(1024 * 1024).await()
            emit(bytes)
        }
    }
}
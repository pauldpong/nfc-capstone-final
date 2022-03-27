package com.capstone.nfc.data

import android.net.Uri
import android.util.Log
import com.capstone.nfc.Constants
import com.capstone.nfc.Constants.FILES_REF
import com.capstone.nfc.Constants.USERS_REF
import com.capstone.nfc.data.Response.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Document
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val dataSource: FirebaseFileDataSource,
    @Named(FILES_REF) private val filesRef: CollectionReference,
    @Named(USERS_REF) private val usersRef: CollectionReference
) {
    val files: Flow<List<StorageFile>> = dataSource.getFiles()

    fun uploadFile(uri: Uri, fullFileName: String) = dataSource.uploadFile(uri, fullFileName)

    fun getFile(filePath: String) = dataSource.getFile(filePath)

    fun getFileMetadata(filePath: String) = flow {
        try {
            emit(Loading)
            emit(Success(filesRef.document(filePath).get().await().toObject<FileMetadata>()!!))
        } catch (e: Exception) {
            emit(Failure(e.message ?: Constants.DEFAULT_ERROR_MESSAGE))
        }

    }

    fun revokeAccess(fileUUID: String, uid: String) = flow {
        emit(usersRef.document(uid).update("sharedWithMe", FieldValue.arrayRemove(filesRef.document(fileUUID))).await())
        emit(filesRef.document(fileUUID).update("accessors", FieldValue.arrayRemove(uid)).await())
    }

    fun addAccessor(fileUUID: String, requesterUid: String) {
        filesRef.document(fileUUID).get().addOnSuccessListener { document ->
            if (document != null) {
                val metadata = document.toObject<FileMetadata>()
                metadata?.let {
                    filesRef.document(fileUUID).update("accessors", FieldValue.arrayUnion(requesterUid))
                }
            }
        }
    }
}
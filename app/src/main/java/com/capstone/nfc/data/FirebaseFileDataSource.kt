package com.capstone.nfc.data

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFileDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
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
                val reference = storage.reference.child(uid + '/' + fileName)
                val snapshot = reference.putFile(uri).await()
                emit(Response.Success(snapshot.metadata))
            }
        } catch (e: Exception) {
            emit(Response.Failure())
        }
    }
}
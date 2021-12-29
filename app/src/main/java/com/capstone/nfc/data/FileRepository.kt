package com.capstone.nfc.data

import android.net.Uri
import android.util.Log
import com.capstone.nfc.Constants
import com.capstone.nfc.data.Response.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    private val storage: FirebaseStorage
) {
    fun uploadFile(uri: Uri) = flow {
        try {
            emit(Loading)
            var reference = storage.reference.child(uri.lastPathSegment.toString())
            val snapshot = reference.putFile(uri).await()
            emit(Success(snapshot.metadata))
        } catch (e: Exception) {
            emit(Failure(e.message ?: Constants.DEFAULT_ERROR_MESSAGE))
        }
    }

}
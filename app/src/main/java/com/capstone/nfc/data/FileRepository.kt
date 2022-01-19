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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    private val dataSource: FirebaseFileDataSource,
    @Named(FILES_REF) private val filesRef: CollectionReference

) {
    val files: Flow<List<File>> =
        dataSource.getFiles()
            .map { references -> references.map { File.from(it) } }

    fun uploadFile(uri: Uri, fileName: String) = dataSource.uploadFile(uri, fileName)

    fun getFile(filePath: String) = dataSource.getFile(filePath)

    fun addAccessor(fileName: String, requesterUid: String) {
        Log.e("test", fileName + " "  + requesterUid)
        filesRef.document(fileName).get().addOnSuccessListener { document ->
            if (document != null) {
                val metadata = document.toObject<FileMetadata>()
                metadata?.let {
                    it.accessors.add(requesterUid)
                    filesRef.document(fileName).update("accessors", metadata.accessors)
                }
            }
        }
    }
}
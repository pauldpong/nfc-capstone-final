package com.capstone.nfc.data

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.*

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @Named(USERS_REF) private val usersRef: CollectionReference,
    @Named(FILES_REF) private val filesRef: CollectionReference,
) {
    val uid get() = auth.currentUser?.uid

    fun getUser() = flow {
        try {
            emit(Loading)
            auth.currentUser?.apply {
                val user = usersRef.document(uid).get().await().toObject<User>()
                user?.let {
                    emit(Success(user))
                }
            }
        } catch (e: Exception) {
            emit(Failure(e.message ?: Constants.DEFAULT_ERROR_MESSAGE))
        }
    }

    fun createUser() = flow {
        try {
            emit(Loading)
            auth.currentUser?.apply { // If user is logged in, currentUser != null
                val newUser = User("Anon" + (System.currentTimeMillis() / 1000).toString(), "test@gmail.com", "123-456-7891")
                usersRef.document(uid).set(newUser).await().also {
                    emit(Success(it))
                }
            }
        } catch (e: Exception) {
            emit(Failure(e.message ?: Constants.DEFAULT_ERROR_MESSAGE))
        }
    }

//    fun getSharedFiles() = callbackFlow {
//        auth.currentUser?.apply {
//            val user = usersRef.document(uid).get().await().toObject<User>()
//            user?.let {
//                val sharedFiles = mutableMapOf<String, FileMetadata>()
//                for (file in user.sharedWithMe) {
//                    val fileMetadata : FileMetadata = file.get().await().toObject()!!
//                    sharedFiles[fileMetadata.path] = fileMetadata
//
//                    // wrong
//                    val subscription = file.addSnapshotListener { value, error ->
//                        value?.let {
//                            if (it.exists()) {
//                                sharedFiles[fileMetadata.path] = it.toObject()!!
//                                offer(sharedFiles)
//                            }
//                        }
//                    }
//
//                    awaitClose { subscription.remove() }
//                }
//                offer(sharedFiles)
//            }
//        }
//    }

    fun getSharedFiles() = callbackFlow {
        auth.currentUser?.apply {
            val userSubscription = usersRef.document(uid).addSnapshotListener { value, error ->
                val flow = flow {
                    emit(Loading)
                    val user = value?.toObject<User>()
                    user?.sharedWithMe?.let {
                        val sharedFiles = mutableListOf<FileMetadata>()
                        for (file in it) {
                            val fileMetadata : FileMetadata = file.get().await().toObject()!!
                            sharedFiles.add(fileMetadata)
                        }
                        emit(Success(sharedFiles))
                    }
                }

                offer(flow)
            }

            awaitClose { userSubscription.remove() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserContacts() = callbackFlow {
        auth.currentUser?.apply {
            val subscription = usersRef.document(uid).addSnapshotListener { value, error ->
                offer(Success(value))
            }

            awaitClose { subscription.remove() }
        }
    }

    // TODO: temp
    fun addAccessor(requesterUid: String) {
        auth.currentUser?.apply {
//            usersRef.document(uid).get().addOnSuccessListener { document ->
//                if (document != null) {
//                    val user = document.toObject<User>()
//                    user?.accessors?.put(requesterUid, true)
//                    usersRef.document(uid).update("accessors", user?.accessors)
//                }
//            }
        }
    }

    fun addFile(fileId: String) {
        auth.currentUser?.apply {
            usersRef.document(uid).update("myFiles", FieldValue.arrayUnion(fileId))
        }
    }

    /**
     * Adds data associated with the *accessToken* to the current users *sharedWithMe* field.
     * Requires the current user to be authorized.
     *
     * @param accessToken Access token from sharing user obtained by NFC reader.
     */
    fun addShared(accessToken: String) {
        auth.currentUser?.apply {
            usersRef.document(uid).update("sharedWithMe", FieldValue.arrayUnion(filesRef.document(accessToken)))
        }
    }
}
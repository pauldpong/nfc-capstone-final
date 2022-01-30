package com.capstone.nfc.data

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

    fun getSharedFiles() = flow {
        auth.currentUser?.apply {
            val user = usersRef.document(uid).get().await().toObject<User>()
            user?.let {
                emit(user.sharedWithMe)
            }
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
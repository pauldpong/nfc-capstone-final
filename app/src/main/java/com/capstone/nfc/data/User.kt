package com.capstone.nfc.data

import com.google.firebase.firestore.DocumentReference

data class User(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val sharedWithMe: List<DocumentReference> = emptyList()
)
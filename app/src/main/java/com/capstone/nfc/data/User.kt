package com.capstone.nfc.data

data class User(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val myFiles: List<String> = emptyList(),
    val sharedWithMe: List<String> = emptyList()
)
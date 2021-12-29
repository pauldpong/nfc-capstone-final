package com.capstone.nfc.data

data class User(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val accessors: HashMap<String, Boolean> = HashMap(),
    val shared: HashMap<String, Boolean> = HashMap()
)
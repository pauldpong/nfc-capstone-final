package com.capstone.nfc.data

data class FileMetadata(
    val name: String = "",
    val type: String = "",
    val path: String = "",
    val owner: String = "",
    val accessors: MutableList<String> = mutableListOf()
)
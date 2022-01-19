package com.capstone.nfc.data

data class FileMetadata(
    val owner: String,
    val accessors: MutableList<String>
) {
    constructor() : this("", mutableListOf())
}
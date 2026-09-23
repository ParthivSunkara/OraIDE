package com.example.oraide.data

import androidx.documentfile.provider.DocumentFile

data class FileNode(
    val file: DocumentFile,
    val name: String = file.name ?: "Unknown",
    val isDirectory: Boolean = file.isDirectory,
    val path: String = file.uri.toString(),
    var children: List<FileNode>? = null, // null means not loaded yet (lazy loading)
    var isExpanded: Boolean = false,
    var level: Int = 0 // Represents the nesting depth in the Explorer tree
) {
    val extension: String
        get() = name.substringAfterLast('.', "")
}

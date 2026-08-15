package com.example.oraide.data

import androidx.documentfile.provider.DocumentFile

interface FileRepository {
    fun getWorkspaceRoot(): DocumentFile?
    fun setWorkspaceRoot(uriString: String)
    suspend fun getChildren(parent: DocumentFile): List<FileNode>
    suspend fun createFile(parent: DocumentFile, name: String): DocumentFile?
    suspend fun createFolder(parent: DocumentFile, name: String): DocumentFile?
    suspend fun rename(file: DocumentFile, newName: String): DocumentFile?
    suspend fun delete(file: DocumentFile): Boolean
    suspend fun readFileContent(file: DocumentFile): String
    suspend fun writeFileContent(file: DocumentFile, content: String)
}

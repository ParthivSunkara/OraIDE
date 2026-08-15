package com.example.oraide.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class SafFileRepository(private val context: Context) : FileRepository {

    private var workspaceRoot: DocumentFile? = null

    override fun getWorkspaceRoot(): DocumentFile? = workspaceRoot

    override fun setWorkspaceRoot(uriString: String) {
        val uri = Uri.parse(uriString)
        workspaceRoot = DocumentFile.fromTreeUri(context, uri)
    }

    override suspend fun getChildren(parent: DocumentFile): List<FileNode> = withContext(Dispatchers.IO) {
        if (!parent.exists() || !parent.isDirectory) return@withContext emptyList()
        parent.listFiles().map { FileNode(it) }.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
    }

    override suspend fun createFile(parent: DocumentFile, name: String): DocumentFile? = withContext(Dispatchers.IO) {
        parent.createFile("*/*", name)
    }

    override suspend fun createFolder(parent: DocumentFile, name: String): DocumentFile? = withContext(Dispatchers.IO) {
        parent.createDirectory(name)
    }

    override suspend fun rename(file: DocumentFile, newName: String): DocumentFile? = withContext(Dispatchers.IO) {
        if (file.renameTo(newName)) {
            file
        } else {
            null
        }
    }

    override suspend fun delete(file: DocumentFile): Boolean = withContext(Dispatchers.IO) {
        file.delete()
    }

    override suspend fun readFileContent(file: DocumentFile): String = withContext(Dispatchers.IO) {
        if (file.exists() && !file.isDirectory) {
            try {
                context.contentResolver.openInputStream(file.uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).readText()
                } ?: ""
            } catch (e: Exception) {
                "Error reading file: ${e.message}"
            }
        } else ""
    }

    override suspend fun writeFileContent(file: DocumentFile, content: String): Unit = withContext(Dispatchers.IO) {
        if (file.exists() && !file.isDirectory) {
            try {
                context.contentResolver.openOutputStream(file.uri, "wt")?.use { outputStream ->
                    outputStream.write(content.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

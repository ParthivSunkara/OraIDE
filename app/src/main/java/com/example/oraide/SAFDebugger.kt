package com.example.oraide

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile

object SAFDebugger {
    fun debugRename(context: Context, oldTreeUriStr: String, newName: String): String {
        val oldUri = Uri.parse(oldTreeUriStr)
        val docFile = DocumentFile.fromTreeUri(context, oldUri)
        if (docFile == null || !docFile.exists()) return "Old tree doesn't exist"
        
        val success = docFile.renameTo(newName)
        val newUri = docFile.uri
        val permissions = context.contentResolver.persistedUriPermissions.joinToString("\n") { it.uri.toString() }
        
        return "Success: $success\nNew URI from docFile: $newUri\nPersisted Permissions:\n$permissions"
    }
}

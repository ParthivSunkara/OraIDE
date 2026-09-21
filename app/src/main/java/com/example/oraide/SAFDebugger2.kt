package com.example.oraide

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile

object SAFDebugger2 {
    fun debug(context: Context, oldUriStr: String, newName: String): String {
        val oldUri = Uri.parse(oldUriStr)
        val docFile = DocumentFile.fromTreeUri(context, oldUri)
        if (docFile == null || !docFile.exists()) return "Old tree doesn't exist"
        
        docFile.renameTo(newName)
        
        val newDocUri = docFile.uri
        var newTreeUriStr = oldUriStr
        val pathSegments = newDocUri.pathSegments
        val newDocId = if (pathSegments.size >= 4 && pathSegments[0] == "tree" && pathSegments[2] == "document") {
            pathSegments[3]
        } else if (pathSegments.size >= 2 && pathSegments[0] == "document") {
            pathSegments[1]
        } else if (pathSegments.size >= 2 && pathSegments[0] == "tree") {
            pathSegments[1]
        } else null
        
        if (newDocId != null && newDocUri.authority != null) {
            newTreeUriStr = android.provider.DocumentsContract.buildTreeDocumentUri(newDocUri.authority, newDocId).toString()
        }
        
        val newDocFile = DocumentFile.fromTreeUri(context, Uri.parse(newTreeUriStr))
        val name = newDocFile?.name
        val childrenCount = newDocFile?.listFiles()?.size ?: -1
        
        return "Name: $name, Children: $childrenCount"
    }
}

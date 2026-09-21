package com.example.oraide

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract

object SAFHelper {
    fun findUpdatedTreeUri(context: Context, oldTreeUri: Uri): Uri {
        val permissions = context.contentResolver.persistedUriPermissions
        for (perm in permissions) {
            // Just return the first one as a test
        }
        return oldTreeUri
    }
}

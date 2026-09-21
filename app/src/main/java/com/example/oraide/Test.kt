package com.example.oraide

import android.net.Uri

fun main() {
    val oldTreeUriStr = "content://com.android.externalstorage.documents/tree/primary%3Aprojects123"
    val docUriStr = "content://com.android.externalstorage.documents/document/primary%3AProjects"
    
    val newDocUri = Uri.parse(docUriStr)
    var newTreeUriStr = oldTreeUriStr
    
    val pathSegments = newDocUri.pathSegments
    val newDocId = if (pathSegments.size >= 4 && pathSegments[0] == "tree" && pathSegments[2] == "document") {
        pathSegments[3]
    } else if (pathSegments.size >= 2 && pathSegments[0] == "document") {
        pathSegments[1]
    } else if (pathSegments.size >= 2 && pathSegments[0] == "tree") {
        pathSegments[1]
    } else null
    
    val authority = newDocUri.authority
    if (newDocId != null && authority != null) {
        newTreeUriStr = "content://" + authority + "/tree/" + Uri.encode(newDocId)
    }
    println(newTreeUriStr)
}

import android.net.Uri

fun main() {
    val newTreeUriStr = "content://com.android.externalstorage.documents/tree/primary%3ADownload%2FProjects"
    
    // Project Switcher logic
    val rawNameSwitcher = Uri.parse(newTreeUriStr).lastPathSegment ?: newTreeUriStr
    val nameSwitcher = rawNameSwitcher.substringAfterLast(":")
    
    // FileExplorerViewModel logic
    // fromTreeUri constructs mUri by extracting documentId and building a document URI
    // For ExternalStorageProvider, documentId is primary:Download/Projects
    val mUriStr = "content://com.android.externalstorage.documents/tree/primary%3ADownload%2FProjects/document/primary%3ADownload%2FProjects"
    val rawNameExplorer = Uri.parse(mUriStr).lastPathSegment ?: mUriStr
    val nameExplorer = rawNameExplorer.substringAfterLast(":")
    
    println("Switcher: \")
    println("Explorer: \")
}

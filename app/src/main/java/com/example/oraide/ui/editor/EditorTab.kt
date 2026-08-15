package com.example.oraide.ui.editor

import androidx.compose.ui.text.input.TextFieldValue
import androidx.documentfile.provider.DocumentFile

data class EditorTab(
    val file: DocumentFile,
    var content: TextFieldValue = TextFieldValue(""),
    var isDirty: Boolean = false,
    val undoManager: UndoManager = UndoManager()
) {
    val title: String get() = if (isDirty) "${file.name ?: "Unknown"} *" else file.name ?: "Unknown"
}

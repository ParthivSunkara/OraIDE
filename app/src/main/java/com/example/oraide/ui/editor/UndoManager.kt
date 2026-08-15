package com.example.oraide.ui.editor

import androidx.compose.ui.text.input.TextFieldValue

class UndoManager(private val maxHistory: Int = 100) {
    private val undoStack = mutableListOf<TextFieldValue>()
    private val redoStack = mutableListOf<TextFieldValue>()
    
    private var isUndoing = false

    fun push(value: TextFieldValue) {
        if (isUndoing) {
            isUndoing = false
            return
        }

        // Don't push if it's identical to the top of the stack
        if (undoStack.isNotEmpty() && undoStack.last().text == value.text) {
            // Keep the latest selection if text is the same
            undoStack[undoStack.size - 1] = value
            return
        }

        undoStack.add(value)
        if (undoStack.size > maxHistory) {
            undoStack.removeAt(0)
        }
        redoStack.clear()
    }

    fun undo(currentValue: TextFieldValue): TextFieldValue? {
        if (undoStack.size > 1) {
            val currentState = undoStack.removeLast()
            redoStack.add(currentState)
            isUndoing = true
            return undoStack.last()
        }
        return null
    }

    fun redo(): TextFieldValue? {
        if (redoStack.isNotEmpty()) {
            val state = redoStack.removeLast()
            undoStack.add(state)
            isUndoing = true
            return state
        }
        return null
    }
}

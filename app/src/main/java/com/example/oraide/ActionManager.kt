package com.example.oraide

import androidx.compose.ui.input.key.*

enum class OraCommand {
    SAVE,
    SAVE_AS,
    CLOSE_TAB,
    CLOSE_ALL_TABS,
    FIND,
    FIND_REPLACE,
    GO_TO_LINE,
    UNDO,
    REDO,
    NEXT_TAB,
    PREV_TAB
}

data class ShortcutDefinition(
    val command: OraCommand,
    val key: Key,
    val isCtrlPressed: Boolean = false,
    val isShiftPressed: Boolean = false,
    val isAltPressed: Boolean = false,
    val description: String
) {
    fun matches(event: KeyEvent): Boolean {
        return event.type == KeyEventType.KeyDown &&
                event.key == key &&
                event.isCtrlPressed == isCtrlPressed &&
                event.isShiftPressed == isShiftPressed &&
                event.isAltPressed == isAltPressed
    }
    
    val shortcutText: String
        get() = buildString {
            if (isCtrlPressed) append("Ctrl + ")
            if (isAltPressed) append("Alt + ")
            if (isShiftPressed) append("Shift + ")
            append(key.toDisplayName())
        }
}

fun Key.toDisplayName(): String {
    return when (this) {
        Key.A -> "A"
        Key.B -> "B"
        Key.C -> "C"
        Key.D -> "D"
        Key.E -> "E"
        Key.F -> "F"
        Key.G -> "G"
        Key.H -> "H"
        Key.I -> "I"
        Key.J -> "J"
        Key.K -> "K"
        Key.L -> "L"
        Key.M -> "M"
        Key.N -> "N"
        Key.O -> "O"
        Key.P -> "P"
        Key.Q -> "Q"
        Key.R -> "R"
        Key.S -> "S"
        Key.T -> "T"
        Key.U -> "U"
        Key.V -> "V"
        Key.W -> "W"
        Key.X -> "X"
        Key.Y -> "Y"
        Key.Z -> "Z"
        Key.Tab -> "Tab"
        Key.Enter -> "Enter"
        Key.Spacebar -> "Space"
        Key.Backspace -> "Backspace"
        Key.Delete -> "Delete"
        Key.Escape -> "Esc"
        else -> "Unknown"
    }
}

object ActionManager {
    val shortcuts = listOf(
        ShortcutDefinition(OraCommand.SAVE, Key.S, isCtrlPressed = true, description = "Save"),
        ShortcutDefinition(OraCommand.SAVE_AS, Key.S, isCtrlPressed = true, isShiftPressed = true, description = "Save As"),
        ShortcutDefinition(OraCommand.CLOSE_TAB, Key.W, isCtrlPressed = true, description = "Close active tab"),
        ShortcutDefinition(OraCommand.CLOSE_ALL_TABS, Key.W, isCtrlPressed = true, isShiftPressed = true, description = "Close all tabs"),
        ShortcutDefinition(OraCommand.FIND, Key.F, isCtrlPressed = true, description = "Find"),
        ShortcutDefinition(OraCommand.FIND_REPLACE, Key.H, isCtrlPressed = true, description = "Find & Replace"),
        ShortcutDefinition(OraCommand.GO_TO_LINE, Key.G, isCtrlPressed = true, description = "Go to Line"),
        ShortcutDefinition(OraCommand.UNDO, Key.Z, isCtrlPressed = true, description = "Undo"),
        ShortcutDefinition(OraCommand.REDO, Key.Y, isCtrlPressed = true, description = "Redo"),
        ShortcutDefinition(OraCommand.REDO, Key.Z, isCtrlPressed = true, isShiftPressed = true, description = "Redo"),
        ShortcutDefinition(OraCommand.NEXT_TAB, Key.Tab, isCtrlPressed = true, description = "Next tab"),
        ShortcutDefinition(OraCommand.PREV_TAB, Key.Tab, isCtrlPressed = true, isShiftPressed = true, description = "Previous tab")
    )

    fun handleKeyEvent(event: KeyEvent, onCommand: (OraCommand) -> Boolean): Boolean {
        for (shortcut in shortcuts) {
            if (shortcut.matches(event)) {
                return onCommand(shortcut.command)
            }
        }
        return false
    }
}

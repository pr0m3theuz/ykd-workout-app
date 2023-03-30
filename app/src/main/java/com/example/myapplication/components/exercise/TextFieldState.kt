package com.example.myapplication.components.exercise

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue

open class TextFieldState() {
    var text: String by mutableStateOf("")

    // was the TextField ever focused
    var isFocusedDirty: Boolean by mutableStateOf(false)
    var isFocused: Boolean by mutableStateOf(false)
    fun onFocusChange(focused: Boolean) {
        isFocused = focused
        if (focused) isFocusedDirty = true
    }
}

fun textFieldStateSaver(state: TextFieldState) = listSaver<TextFieldState, Any>(
    save = { listOf(it.text, it.isFocusedDirty) },
    restore = {
        state.apply {
            text = it[0] as String
            isFocusedDirty = it[1] as Boolean
        }
    }
)

package com.example.myapplication.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

// @Preview
@Composable
fun SearchBar(
    filterExercises: (search: String) -> Unit
) {
    var search by remember { mutableStateOf(TextFieldValue("")) }

    Box(modifier = Modifier.fillMaxWidth(0.9f)) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = search,
            onValueChange = {
                search = it
                filterExercises("%${search.text}%")
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        )
    }
}

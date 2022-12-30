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
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun SearchBar() {
    var search by remember { mutableStateOf(TextFieldValue("")) }

    Box(modifier = Modifier.fillMaxWidth(0.9f)) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = search,
            onValueChange = { search = it },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        )
    }
}

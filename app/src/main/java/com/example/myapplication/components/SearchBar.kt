package com.example.myapplication.components

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

// @Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySearchBar(
    filterExercises: (search: String) -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    val color = SearchBarDefaults.colors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        inputFieldColors = TextFieldDefaults.colors(
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary
        )
    )

    androidx.compose.material3.SearchBar(
        query = text,
        onQueryChange = {
            text = it
            filterExercises("%$it%")
        },
        onSearch = { active = false },
        active = active,
        colors = color,
        onActiveChange = { active = it },
        placeholder = { Text("Search Exercises") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
    ) {
    }
}

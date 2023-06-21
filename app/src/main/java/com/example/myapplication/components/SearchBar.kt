package com.example.myapplication.components

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
//    var search by remember { mutableStateOf(TextFieldValue("")) }

//    Box(modifier = Modifier.fillMaxWidth()) {
//        val textFieldColors = TextFieldDefaults.textFieldColors(
//            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
//            cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
//            focusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
//            leadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
//            textColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        )
//
//        TextField(
//            colors = textFieldColors,
//            modifier = Modifier.fillMaxWidth(),
//            value = search,
//            onValueChange = {
//                search = it
//                filterExercises("%${search.text}%")
//            },
//            textStyle = TextStyle(fontSize = 18.sp),
//            leadingIcon = {
//                Icon(
//                    Icons.Default.Search,
//                    contentDescription = "Search Icon"
//                )
//            }
//        )
//    }

    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    androidx.compose.material3.SearchBar(
        query = text,
        onQueryChange = {
            text = it
            filterExercises("%$it%")
        },
        onSearch = { active = false },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text("Search Exercises") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
    ) {
    }
}

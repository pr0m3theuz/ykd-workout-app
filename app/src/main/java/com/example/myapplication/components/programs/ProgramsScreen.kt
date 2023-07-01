package com.example.myapplication.components.programs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ProgramsScreen() {
    Scaffold(
        topBar = { /* TODO */ },
        bottomBar = { /* TODO */ }
    ) { _ ->
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(text = "Programs")
            WorkoutSearchBar()

            Text("Continue Program")
            CurrentProgramCard()

            Text("Quick Start")
            Button(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Localized Description"
                )
                Text(text = "Start An Empty Workout")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Programs")
                Box() {
                    Row() {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Outlined.FileUpload,
                                contentDescription = "Import/Export New Program"
                            )
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Outlined.CreateNewFolder,
                                contentDescription = "Add New Program"
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.Assignment,
                        contentDescription = "Add New Workout"
                    )
                    Text(text = "New Workout")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = "Show workouts for this program"
                    )
                }
                Text(text = "Program Name (Workout Count)")
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Localized Description"
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = "Show workouts"
                    )
                }
                Text(text = "Workouts (Workout Count)")
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Localized Description"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSearchBar() {
//    val context = LocalContext.current
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    val color = SearchBarDefaults.colors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        inputFieldColors = TextFieldDefaults.colors(
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary
        )
    )

    SearchBar(
        query = text,
        onQueryChange = {
            text = it
// TODO filterWorkouts("%$it%")
            // TODO filterPrograms("%$it%")
        },
        onSearch = { active = false },
        active = active,
        colors = color,
        onActiveChange = { active = false },
        placeholder = {
            androidx.compose.material.Text(
                "Search Exercises",
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        },
        leadingIcon = {
            androidx.compose.material.Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        },
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {}
}

@Composable
fun WorkoutCard() {
}

@Composable
fun ProgramCard() {
}

// @Preview
@Composable
fun CurrentProgramCard() {
    Card() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Program Title")
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Localized Description"
                )
            }
        }
        Text(text = "Microcycle Week # Day #")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Exercises")
            Text(text = "Sets")
            Text(text = "Top Set")
            Text(text = "RIR")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Start Microcycle Week # Day #")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Outlined.LibraryBooks,
                    contentDescription = "Localized Description"
                )
            }
        }
    }
}

package com.example.myapplication.components.programs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.BrokenImage
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
// @Preview
@Composable
fun ProgramsScreen(navController: NavController) {
    val padding8 = 8.dp
    val titleMedium = MaterialTheme.typography.titleMedium
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Programs", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Go to the previous screen"
                        )
                    }
                },
                actions = {
                    ProgramDetailsOptionsDropdownMenu()
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(contentPadding)
        ) {
            WorkoutSearchBar()
            Column(modifier = Modifier.padding(12.dp).verticalScroll(scrollState)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(padding8)
                ) { Text("Continue Program", style = titleMedium) }

                CurrentProgramCard(navController)

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(padding8)
                ) { Text("Quick Start", style = titleMedium) }

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
                    Text(text = "Programs", style = titleMedium)

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
                    Text(text = "Program Name/Macrocycle (Workout Count)")
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "Localized Description"
                        )
                    }
                }

                ProgramCard(navController)

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
                    Text(text = "Mesocycle (Workout Count)")
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
                WorkoutCard()
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

// @Preview
@Composable
fun WorkoutCard() {
    val padding = 8.dp
    val titleMedium = MaterialTheme.typography.titleMedium

    Card(modifier = Modifier.clickable { /* TODO Navigate to Workout Details Screen */ }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(padding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column() {
                Text(text = "Workout Title", style = titleMedium)
                Text(text = "Workout Author", style = MaterialTheme.typography.titleSmall)
                Text(text = "Last performed: Date", style = MaterialTheme.typography.bodySmall)
            }
//            TODO To Keep or not to not Keep
            Column(verticalArrangement = Arrangement.Top) {
                IconButton(onClick = { /* TODO Open WorkoutOptionMenu */ }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Localized Description"
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(padding)) {
            Column() {
//                TODO List Exercises
                Text("Workout Summary", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ProgramCard(navController: NavController) {
    val padding = 8.dp
    val titleMedium = MaterialTheme.typography.titleMedium

    Card(
        modifier = Modifier.clickable {
            /* TODO Navigate to Program Details Screen */ navController.navigate(
                "programDetails"
            )
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(padding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material.Icon(
                imageVector = Icons.Outlined.BrokenImage,
                contentDescription = "Missing Image",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column() {
                Text(text = "Program Title", style = titleMedium)
                Text(text = "Program Author", style = MaterialTheme.typography.titleSmall)
            }
//            TODO To Keep or not to not Keep
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Localized Description"
                )
            }
        }
    }
}

// @Preview
@Composable
fun CurrentProgramCard(navController: NavController) {
    val padding = 8.dp

    val titleSmall = MaterialTheme.typography.titleSmall

    Card() {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Program Title",
                style = titleSmall,
                modifier = Modifier.clickable {
                    /* TODO Navigate to Program Details Screen */
                    navController.navigate("programDetails")
                }
            )
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Localized Description"
                )
            }
        }
        Text(
            text = "Microcycle Week # Day #",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(padding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Exercise", style = titleSmall)
            Box(Modifier.fillMaxWidth(0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Sets", style = titleSmall)
                    Text(text = "Top Set", style = titleSmall)
                    Text(text = "RIR", style = titleSmall)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(padding)) {
            Column() {
//                TODO List Exercises
                Text("Workout Summary", style = MaterialTheme.typography.bodySmall)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(padding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Start Microcycle Week # Day #")
            }
            IconButton(onClick = { /*TODO*/ navController.navigate("programDetails") }) {
                Icon(
                    imageVector = Icons.Outlined.LibraryBooks,
                    contentDescription = "Localized Description"
                )
            }
        }
    }
}

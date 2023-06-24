package com.example.myapplication.components.exerciseDetails

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ExerciseDetailsScreen() {
//

    val context = LocalContext.current
    var state by remember { mutableStateOf(0) }
    val titles = listOf("About", "History", "Charts", "Records")

    Surface(color = MaterialTheme.colorScheme.surface) {
        Column {
            Row() {
                TopAppBar(
                    title = {
                        Text(
                            text = "Exercises", // TODO Replace with Exercise Name
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                /*TODO Navigate to Previous Screen */
                                Toast.makeText(
                                    context,
                                    "TODO: Navigate to Previous Screen",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
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
                        ExerciseDetailsOptionsDropdownMenu()
                    }
                )
            }

            TabRow(selectedTabIndex = state) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = state == index,
                        onClick = { state = index },
                        text = {
                            Text(
                                text = title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }

            when (state) {
                0 -> AboutTab()
                1 -> HistoryTab()
                2 -> ChartTab()
                3 -> RecordsTab()
            }
        }
    }
}

@Composable
fun ExerciseDetailsOptionsDropdownMenu() {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = "Exercise Options and Settings",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { androidx.compose.material3.Text("Edit Exercise") },
                onClick = { /* TODO Navigate to Create Exercise Screen*/ }
            )
        }
    }
}

@Preview
@Composable
fun AboutTab() {
    val exerciseName: String = "skrillex rumble".replace(" ", "+")
    val youtubeSearchQuery = "https://www.youtube.com/results?search_query=$exerciseName"

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeSearchQuery))
//    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    val context = LocalContext.current

    Column() {
        Row() {
            Icon(imageVector = Icons.Outlined.BrokenImage, contentDescription = "Missing Image")
        }
        Button(onClick = { startActivity(context, intent, null) }) {
            Text(text = "Search Exercise on Youtube")
        }
    }
}

@Preview
@Composable
fun HistoryTab() {
//    TODO Write query to get Workout Data for an Exercise, return a List
//    TODO Change to LazyColumn
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = "Workout Name")
            Text(text = "Program Name")
            Text(text = "Mesocycle/Microcycle")
            Text(text = "Workout Date & Time")
            Text(text = "Total Volume: 9000 kg")
            Row {
                Text(text = "Sets performed")
            }
            Text(text = "List of Sets")
        }
    }
}

@Preview
@Composable
fun ChartTab() {
    Column() {
        Row() {
            Icon(imageVector = Icons.Outlined.BrokenImage, contentDescription = "Missing Image")
        }
    }
}

@Preview
@Composable
fun RecordsTab() {
    val rowPadding = 4.dp

    Column(Modifier.padding(8.dp).fillMaxWidth()) {
        Row(Modifier.padding(rowPadding)) {
            Text("Personal Records")
        }
        Row(
            Modifier.padding(rowPadding).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Estimated 1RM")
            Text("13 lbs")
        }
        Row(
            Modifier.padding(rowPadding).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Max Volume")
            Text("1,300 lbs")
        }
        Row(
            Modifier.padding(rowPadding).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Max weight")
            Text("13 lbs")
        }
        Row(
            Modifier.padding(rowPadding).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { /* TODO Navigate Records History */ }) {
                Text(text = "VIEW RECORDS HISTORY")
            }
        }
        Row(
            Modifier.padding(rowPadding).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Reps")
            Text("Best Performance")
            Text("Estimated 1RM")
        }

        // TODO Refoctor to List
        Row(
            Modifier.padding(rowPadding).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box() {
                Text(text = "1")
            }
            Box() {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "85 lbs (# of Reps)")
                    Text(text = "Date")
                }
            }
            Box() { Text(text = "1RM") }
        }

        Row(Modifier.padding(rowPadding)) {
            Text(text = "Estimate RMs are generated using ...")
        }
        Row(Modifier.padding(rowPadding)) {
            Text("LIFETIME STATS")
        }
        Row(
            Modifier.padding(rowPadding).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total Reps")
            Text(text = "1,305")
        }
        Row(
            Modifier.padding(rowPadding).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total Volume")
            Text("13,405 lbs")
        }
    }
}

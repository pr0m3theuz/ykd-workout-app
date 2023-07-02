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
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.components.exercisesList.ExercisesViewModel
import com.example.myapplication.data.ExerciseModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailsScreen(
    navController: NavController,
    exercisePK: String,
    exercisesViewModel: ExercisesViewModel,
) {
    exercisesViewModel.getExerciseDetails(exercisePK)

    val exercise by exercisesViewModel.selectedExerciseDetails.collectAsStateWithLifecycle()

    ExerciseDetailsLayout(navController, exercise)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ExerciseDetailsLayout(navController: NavController, _exercise: List<ExerciseModel>) {
    val exercise = _exercise.firstOrNull()

    val context = LocalContext.current
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("About", "History", "Charts", "Records")
    val onSurface = MaterialTheme.colorScheme.onSurface

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = exercise?.name ?: "Loading...",
                        maxLines = 2,
                        overflow = TextOverflow.Visible,
                        color = onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
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
    ) { contentPadding ->
        Column(Modifier.padding(contentPadding)) {
            TabRow(
                selectedTabIndex = state,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
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

            Column(Modifier.padding(8.dp).fillMaxWidth()) {
                when (state) {
                    0 -> AboutTab(exercise?.name ?: "never gonna give you up")
                    1 -> HistoryTab()
                    2 -> ChartTab()
                    3 -> RecordsTab()
                }
            }
        }
    }
}

@Composable
fun ExerciseDetailsOptionsDropdownMenu() {
    var expanded by remember { mutableStateOf(false) }

    val onSurface = MaterialTheme.colorScheme.onSurface

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = "Exercise Options and Settings",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit Exercise", color = onSurface) },
                onClick = { /* TODO Navigate to Create Exercise Screen*/ }
            )
        }
    }
}

// @Preview
@Composable
fun AboutTab(_exerciseName: String) {
    val exerciseName: String = _exerciseName.replace(" ", "+")
    val youtubeSearchQuery = "https://www.youtube.com/results?search_query=$exerciseName"

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeSearchQuery))

    val context = LocalContext.current

    val onSurface = MaterialTheme.colorScheme.onSecondaryContainer

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Row() {
            Icon(
                imageVector = Icons.Outlined.BrokenImage,
                contentDescription = "Missing Image",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Button(
            onClick = { startActivity(context, intent, null) },
            colors = buttonColors,
        ) {
            Text(text = "Search Exercise on Youtube", color = onSurface)
        }
    }
}

@Preview
@Composable
fun HistoryTab() {
//    TODO Write query to get Workout Data for an Exercise, return a List
//    TODO Change to LazyColumn
    val cardColors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme
                .primaryContainer.copy(alpha = 0.2f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    val subtitle = MaterialTheme.colorScheme.onSurfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Card(
        colors = cardColors,
        modifier = Modifier
            .fillMaxWidth(),
//            .padding(8.dp)
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Workout Name",
                style = MaterialTheme.typography.titleMedium,
                color = onSurface
            )
            Text(
                text = "Program Name",
                style = MaterialTheme.typography.bodyMedium,
                color = subtitle
            )
            Text(
                text = "Mesocycle/Microcycle",
                style = MaterialTheme.typography.bodyMedium,
                color = subtitle
            )
            Text(
                text = "Workout Date & Time",
                style = MaterialTheme.typography.bodyMedium,
                color = subtitle
            )
            Text(text = "Total Volume: 9000 kg", color = onSurface)
            Row(Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Sets performed",
                    style = MaterialTheme.typography.titleSmall,
                    color = onSurface
                )
            }
            Row() {
                Text(
                    text = "List of Sets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitle
                )
            }
        }
    }
}

@Preview
@Composable
fun ChartTab() {
    Column() {
        Row() {
            Icon(
                imageVector = Icons.Outlined.BrokenImage,
                contentDescription = "Missing Image",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun RecordsTab() {
    val rowPadding = 4.dp

    val onSurface = MaterialTheme.colorScheme.onSurface
    val subtitle = MaterialTheme.colorScheme.onSurfaceVariant

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )

    Column(
        Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Row(Modifier.padding(rowPadding)) {
            Text(
                "Personal Records",
                color = onSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            Modifier
                .padding(rowPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Estimated 1RM", color = onSurface)
            Text("13 lbs", color = onSurface)
        }
        Row(
            Modifier
                .padding(rowPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Max Volume", color = onSurface)
            Text("1,300 lbs", color = onSurface)
        }
        Row(
            Modifier
                .padding(rowPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Max Weight", color = onSurface)
            Text("13 lbs", color = onSurface)
        }
        Row(
            Modifier
                .padding(rowPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { /* TODO Navigate Records History */ },
                colors = buttonColors
            ) {
                Text(text = "View Records History", color = onSurface)
            }
        }
        Row(
            Modifier
                .padding(rowPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Reps", color = onSurface, modifier = Modifier.weight(1f))
            Text(
                "Best Performance",
                color = onSurface,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                "Estimated 1RM",
                color = onSurface,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

        // TODO Refoctor to List
        Row(
            Modifier
                .padding(rowPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "1", color = onSurface, modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "85 lbs (# of Reps)", color = onSurface, textAlign = TextAlign.Center)
                Text(
                    text = "Date",
                    color = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "1RM",
                color = onSurface,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

        Row(Modifier.padding(rowPadding)) {
            Text(
                text = "Estimate RMs are generated using ...",
                color = subtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(Modifier.padding(rowPadding)) {
            Text("Lifetime Stats", color = onSurface, style = MaterialTheme.typography.titleMedium)
        }
        Row(
            Modifier
                .padding(rowPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total Reps", color = onSurface)
            Text(text = "1,305", color = onSurface)
        }
        Row(
            Modifier
                .padding(rowPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total Volume", color = onSurface)
            Text("13,405 lbs", color = onSurface)
        }
    }
}

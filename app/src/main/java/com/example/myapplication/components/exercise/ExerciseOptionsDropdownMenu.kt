package com.example.myapplication.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.myapplication.components.exercise.EquateVolumeScreen
import com.example.myapplication.components.setRestTimer.AutoRestTimerScreen
import com.example.myapplication.data.ExerciseSetModel

// import com.example.myapplication.components.RestTimer.RestTimePicker

@Composable
fun ExerciseOptionsDropdownMenu(
    exerciseSets: ExerciseSetModel,
    addNotes: () -> Unit,
    removeExercise: () -> Unit
) {
    var expandedExerciseOptionsDropdownMenu by remember { mutableStateOf(false) }
    var expandedWeightUnitOptionsDropdownMenu by remember { mutableStateOf(false) }
    var expandedBarTypeOptionsDropdownMenu by remember { mutableStateOf(false) }
    val openAddWarmUpSetsDialog = remember { mutableStateOf(false) }
    val openAddWorkingSetsDialog = remember { mutableStateOf(false) }
    var openRestTimePicker by remember { mutableStateOf(false) }
    var openReplaceExerciseDialog by remember { mutableStateOf(false) }
    var openEquateVolumeDialog by remember { mutableStateOf(false) }

    fun closeAddWarmUpSetsDialog(close: Boolean) {
        openAddWarmUpSetsDialog.value = close
    }

    fun closeWorkingSetsDialog(close: Boolean) {
        openAddWorkingSetsDialog.value = close
    }

    fun closeRestTimerDialog(close: Boolean) {
        openRestTimePicker = close
    }

    fun closeReplaceExerciseDialog(close: Boolean) {
        openReplaceExerciseDialog = close
    }

    fun closeEquateVolumeDialog(close: Boolean) {
        openEquateVolumeDialog = close
    }

    Box {
        IconButton(onClick = { expandedExerciseOptionsDropdownMenu = true }) {
            Icon(Icons.Outlined.MoreVert, contentDescription = "Exercise Options and Settings")
        }
        DropdownMenu(
            expanded = expandedExerciseOptionsDropdownMenu,
            onDismissRequest = { expandedExerciseOptionsDropdownMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Add Note") },
                onClick = {
                    addNotes()
                    expandedExerciseOptionsDropdownMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Add Warm-up Sets") },
                onClick = { openAddWarmUpSetsDialog.value = true }
            )
            DropdownMenuItem(
                text = { Text("Add Working Sets") },
                onClick = { openAddWorkingSetsDialog.value = true }
            )
            DropdownMenuItem(
                text = { Text("Equate Volume") },
                onClick = { openEquateVolumeDialog = true }
            )
            DropdownMenuItem(
                text = { Text("Create Superset") },
                onClick = { /* TODO */ }
            )
//            DropdownMenuItem(
//                text = { Text("Create Giant Set") },
//                onClick = { /* TODO */ }
//            )
//            DropdownMenuItem(
//                text = { Text("Create Drop Set") },
//                onClick = { /* TODO */ }
//            )
//            DropdownMenuItem(
//                text = { Text("Create Myo-Rep Set") },
//                onClick = { /* TODO */ }
//            )
            Divider()
            DropdownMenuItem(
                text = { Text("Replace Exercise") },
                onClick = { openReplaceExerciseDialog = true }
            )
            Divider()
            DropdownMenuItem(
                text = { Text("Auto Rest Timer") },
                trailingIcon = {
                    Text(
                        exerciseSets.exercise.workingSetRestDuration.toComponents { minutes, seconds, _ ->
                            "$minutes:${seconds.toString().padStart(2, '0')}"
                        }
                    )
                },
                onClick = { openRestTimePicker = true }
            )
            DropdownMenuItem(
                text = { Text("Weight Unit") },
                trailingIcon = { Text("lbs") },
                onClick = {
                    expandedExerciseOptionsDropdownMenu = false
                    expandedWeightUnitOptionsDropdownMenu = true
                }
            )
            DropdownMenuItem(
                text = { Text("Bar Type") },
                trailingIcon = { Text("Olympic Bar") },
                onClick = {
                    expandedExerciseOptionsDropdownMenu = false
                    expandedBarTypeOptionsDropdownMenu = true
                }
            )
            Divider()
            DropdownMenuItem(
                text = { Text("Remove Exercise") },
                onClick = { removeExercise() }
            )
        }
        WeightUnitOptionsDropdownMenu(
            expandMenu = { expandedWeightUnitOptionsDropdownMenu },
            onClickArrowBack = { expandedExerciseOptionsDropdownMenu = true },
            onDismiss = { expandedWeightUnitOptionsDropdownMenu = false }
        )
        BarTypeDropdownMenu(
            expandMenu = { expandedBarTypeOptionsDropdownMenu },
            onClickArrowBack = { expandedExerciseOptionsDropdownMenu = true },
            onDismiss = { expandedBarTypeOptionsDropdownMenu = false }
        )

        // TODO Add exerciseSets to each of the below functions
        if (openAddWarmUpSetsDialog.value) {
            AddWarmUpSetsScreen(
                openDialog = { close -> closeAddWarmUpSetsDialog(close) },
                exerciseSets = exerciseSets
            )
        }
        if (openAddWorkingSetsDialog.value) {
            AddWorkingSetsScreen(
                openDialog = { close -> closeWorkingSetsDialog(close) },
                exerciseSets = exerciseSets
            )
        }

        if (openRestTimePicker) {
            AutoRestTimerScreen(
                openDialog = { close -> closeRestTimerDialog(close) },
                exercise = exerciseSets.exercise
            )
        }

        if (openReplaceExerciseDialog) {
            ReplaceExerciseDialog(
                openDialog = { close -> closeReplaceExerciseDialog(close) }
            )
        }

        if (openEquateVolumeDialog) {
            EquateVolumeScreen(
                openDialog = { close -> closeEquateVolumeDialog(close) },
                exerciseSets = exerciseSets
            )
        }
    }
}

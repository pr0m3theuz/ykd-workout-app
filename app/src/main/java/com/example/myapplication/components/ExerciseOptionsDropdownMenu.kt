package com.example.myapplication.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun exerciseOptionsDropdownMenu() {
    var expandedExerciseOptionsDropdownMenu by remember { mutableStateOf(false) }
    var expandedWeightUnitOptionsDropdownMenu by remember { mutableStateOf(false) }
    var expandedBarTypeOptionsDropdownMenu by remember { mutableStateOf(false) }
    val openAddWarmUpSetsDialog = remember { mutableStateOf(false) }
    val openAddWorkingSetsDialog = remember { mutableStateOf(false) }

    fun closeAddWarmUpSetsDialog(close: Boolean) {
        openAddWarmUpSetsDialog.value = close
    }

    fun closeWorkingSetsDialog(close: Boolean) {
        openAddWorkingSetsDialog.value = close
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
                onClick = { /* TODO */ }
            )
            DropdownMenuItem(
                text = { Text("Add Warm-up Sets") },
                onClick = { openAddWarmUpSetsDialog.value = true }
            )
            DropdownMenuItem(
                text = { Text("Create Working Sets") },
                onClick = { openAddWorkingSetsDialog.value = true }
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
                onClick = { }
            )
            Divider()
            DropdownMenuItem(
                text = { Text("Auto Rest Timer") },
                trailingIcon = { Text("1:30") },
                onClick = { /* TODO */ }
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
                onClick = { /* TODO */ }
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
        if (openAddWarmUpSetsDialog.value) {
            AddWarmUpSetsScreen(openDialog = { close -> closeAddWarmUpSetsDialog(close) })
        }
        if (openAddWorkingSetsDialog.value) {
            AddWorkingSetsScreen(openDialog = { close -> closeAddWarmUpSetsDialog(close) })
        }
    }
}

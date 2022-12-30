package com.example.myapplication.components.exercisesList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.BodyParts
import com.example.myapplication.data.Equipment

@Preview
@Composable
fun ExercisesFilterMenu() {
//    var expanded by remember { mutableStateOf(true) }
    var filterMenuExpanded by remember { mutableStateOf(false) }
    fun expandFilterMenu() {
        filterMenuExpanded = !filterMenuExpanded
    }

    val bodyParts = BodyParts.values().sortedBy { it.name }
    var bodyPartsVisibility by remember { mutableStateOf(false) }

    val equipments = Equipment.values().sortedBy { it.name }
    var equipmentVisibility by remember { mutableStateOf(false) }

    fun expandBodyParts() {
        bodyPartsVisibility = !bodyPartsVisibility
    }

    fun expandEquipment() {
        equipmentVisibility = !equipmentVisibility
    }

    fun closeAll() {
        bodyPartsVisibility = false
        equipmentVisibility = false
    }

    Box(modifier = Modifier.size(40.dp)) {
        IconButton(
            onClick = { expandFilterMenu() }
        ) {
            Icon(
                Icons.Default.FilterList,
                contentDescription = "Filter Exercises"
            )
        }

        DropdownMenu(
            expanded = filterMenuExpanded,
            onDismissRequest = {
                expandFilterMenu()
                closeAll()
            }
        ) {
            DropdownMenuItem(
                text = {
                    Row {
                        Text(text = "Body Parts")
                        Crossfade(targetState = bodyPartsVisibility) { bodyPartsVisibility ->
                            when (bodyPartsVisibility) {
                                true -> Icon(
                                    Icons.Default.ExpandLess,
                                    contentDescription = "Hide body parts"
                                )
                                false -> Icon(
                                    Icons.Default.ExpandMore,
                                    contentDescription = "Show body parts"
                                )
                            }
                        }
                    }
                },
                onClick = { expandBodyParts() }
            )
            AnimatedVisibility(
                visible = bodyPartsVisibility,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    bodyParts.forEach {
                        Row {
                            DropdownMenuItem(
                                text = { Text(text = it.name) },
                                onClick = { /* TODO */ }
                            )
                        }
                    }
                }
            }
            Divider()

            DropdownMenuItem(
                text = {
                    Row {
                        Text(text = "Equipment")
                        Crossfade(targetState = equipmentVisibility) { equipmentVisibility ->
                            when (equipmentVisibility) {
                                true -> Icon(
                                    Icons.Default.ExpandLess,
                                    contentDescription = "Hide equipment"
                                )
                                false -> Icon(
                                    Icons.Default.ExpandMore,
                                    contentDescription = "Show equipment"
                                )
                            }
                        }
                    }
                },
                onClick = { expandEquipment() }
            )
            AnimatedVisibility(
                visible = equipmentVisibility,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    equipments.forEach {
                        Row {
                            DropdownMenuItem(
                                text = { Text(text = it.name) },
                                onClick = { /* TODO */ }
                            )
                        }
                    }
                }
            }

            Divider()

            DropdownMenuItem(
                text = {
                    Row {
                        Text(text = "Movement Types")
                        Crossfade(targetState = equipmentVisibility) { equipmentVisibility ->
                            when (equipmentVisibility) {
                                true -> Icon(
                                    Icons.Default.ExpandLess,
                                    contentDescription = "Hide Movement Types"
                                )
                                false -> Icon(
                                    Icons.Default.ExpandMore,
                                    contentDescription = "Show Movement Types"
                                )
                            }
                        }
                    }
                },
                onClick = { expandEquipment() }
            )
            AnimatedVisibility(
                visible = equipmentVisibility,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    equipments.forEach {
                        Row {
                            DropdownMenuItem(
                                text = { Text(text = it.name) },
                                onClick = { /* TODO */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

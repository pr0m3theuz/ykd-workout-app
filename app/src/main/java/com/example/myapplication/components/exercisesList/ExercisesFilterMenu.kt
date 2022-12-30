package com.example.myapplication.components.exercisesList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Equipment
import com.example.myapplication.data.MovementType
import com.example.myapplication.data.MuscleGroup

@Preview
@Composable
fun ExercisesFilterMenu() {
//    var expanded by remember { mutableStateOf(true) }
    var filterMenuExpanded by remember { mutableStateOf(false) }
    fun expandFilterMenu() {
        filterMenuExpanded = !filterMenuExpanded
    }

    val muscleGroups = MuscleGroup.values().sortedBy { it.name }
    var muscleGroupsVisibility by remember { mutableStateOf(false) }

    val equipments = Equipment.values().sortedBy { it.name }
    var equipmentVisibility by remember { mutableStateOf(false) }

    val movementTypes = MovementType.values().sortedBy { it.name }
    var movementTypeVisibility by remember { mutableStateOf(false) }

    val muscleGroupsChecked = remember { muscleGroups.map { it to false }.toMutableStateMap() }
    val equipmentsChecked = remember { equipments.map { it to false }.toMutableStateMap() }
    val movementTypesChecked = remember { movementTypes.map { it to false }.toMutableStateMap() }

    fun expandMuscleGroups() {
        muscleGroupsVisibility = !muscleGroupsVisibility
    }

    fun expandEquipment() {
        equipmentVisibility = !equipmentVisibility
    }

    fun expandMovementType() {
        movementTypeVisibility = !movementTypeVisibility
    }

    fun collapseOthers() {
        if (muscleGroupsVisibility) {
            muscleGroupsVisibility = false
        }

        if (equipmentVisibility) {
            equipmentVisibility = false
        }

        if (movementTypeVisibility) {
            movementTypeVisibility = false
        }
    }

    fun closeAll() {
        muscleGroupsVisibility = false
        equipmentVisibility = false
        movementTypeVisibility = false
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
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            DropdownMenuItem(
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Muscle Groups")
                        Crossfade(targetState = muscleGroupsVisibility) { muscleGroupsVisibility ->
                            when (muscleGroupsVisibility) {
                                true -> Icon(
                                    Icons.Default.ExpandLess,
                                    contentDescription = "Hide Muscle Groups"
                                )
                                false -> Icon(
                                    Icons.Default.ExpandMore,
                                    contentDescription = "Show Muscle Groups"
                                )
                            }
                        }
                    }
                },
                onClick = {
                    collapseOthers()
                    expandMuscleGroups()
                }
            )
            AnimatedVisibility(
                visible = muscleGroupsVisibility,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    muscleGroups.forEach { muscleGroup ->
                        Row() {
                            fun updateState() {
                                muscleGroupsChecked[muscleGroup] =
                                    !muscleGroupsChecked[muscleGroup]!!
                            }
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = muscleGroup.printableName)
                                        Checkbox(
                                            checked = muscleGroupsChecked[muscleGroup] == true,
                                            onCheckedChange = {
                                                updateState()
//
                                            }
                                        )
                                    }
                                },
                                onClick = {
                                    updateState()
                                }
                            )
                        }
                    }
                }
            }
            Divider()

            DropdownMenuItem(
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
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
                onClick = {
                    collapseOthers()
                    expandEquipment()
                }
            )
            AnimatedVisibility(
                visible = equipmentVisibility,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    equipments.forEach { equipment ->
                        Row {
                            fun updateState() {
                                equipmentsChecked[equipment] = !equipmentsChecked[equipment]!!
                            }
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Text(text = equipment.printableName)
                                        Checkbox(
                                            checked = equipmentsChecked[equipment] == true,
                                            onCheckedChange = {
                                                updateState()
//
                                            }
                                        )
                                    }
                                },
                                onClick = { /* TODO */ }
                            )
                        }
                    }
                }
            }

            Divider()

            DropdownMenuItem(
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Movement Types")
                        Crossfade(targetState = movementTypeVisibility) { movementTypeVisibility ->
                            when (movementTypeVisibility) {
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
                onClick = {
                    collapseOthers()
                    expandMovementType()
                }
            )
            AnimatedVisibility(
                visible = movementTypeVisibility,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    movementTypes.forEach { movementType ->
                        Row {
                            fun updateState() {
                                movementTypesChecked[movementType] =
                                    !movementTypesChecked[movementType]!!
                            }
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Text(text = movementType.printableName)
                                        Checkbox(
                                            checked = movementTypesChecked[movementType] == true,
                                            onCheckedChange = {
                                                updateState()
//
                                            }
                                        )
                                    }
                                },
                                onClick = { /* TODO */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

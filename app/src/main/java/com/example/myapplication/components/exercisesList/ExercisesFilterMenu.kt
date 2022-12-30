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
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
fun ExercisesFilterMenu(exercisesViewModel: ExercisesViewModel = viewModel()) {
//    var expanded by remember { mutableStateOf(true) }
    var filterMenuExpanded by remember { mutableStateOf(false) }
    fun expandFilterMenu() {
        filterMenuExpanded = !filterMenuExpanded
    }

    val muscleGroups = exercisesViewModel.muscleGroups
    var muscleGroupsVisibility by remember { mutableStateOf(false) }

    val equipments = exercisesViewModel.equipments
    var equipmentVisibility by remember { mutableStateOf(false) }

    val movementTypes = exercisesViewModel.movementTypes
    var movementTypeVisibility by remember { mutableStateOf(false) }

//    val muscleGroupsChecked = remember { exercisesViewModel.muscleGroupsChecked }
//    val equipmentsChecked = remember { exercisesViewModel.equipmentsChecked }
//    val movementTypesChecked = remember { exercisesViewModel.movementTypesChecked }

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
                    if (!muscleGroupsVisibility) {
                        collapseOthers()
                    }
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
                                exercisesViewModel.muscleGroupsChecked[muscleGroup] =
                                    !exercisesViewModel.muscleGroupsChecked[muscleGroup]!!
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
                                            checked = exercisesViewModel.muscleGroupsChecked[muscleGroup] == true,
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
                    if (!equipmentVisibility) {
                        collapseOthers()
                    }
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
                                exercisesViewModel.equipmentsChecked[equipment] =
                                    !exercisesViewModel.equipmentsChecked[equipment]!!
                            }
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = equipment.printableName)
                                        Checkbox(
                                            checked = exercisesViewModel.equipmentsChecked[equipment] == true,
                                            onCheckedChange = {
                                                updateState()
//
                                            }
                                        )
                                    }
                                },
                                onClick = { updateState() }
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
                    if (!movementTypeVisibility) {
                        collapseOthers()
                    }
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
                                exercisesViewModel.movementTypesChecked[movementType] =
                                    !exercisesViewModel.movementTypesChecked[movementType]!!
                            }
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = movementType.printableName)
                                        Checkbox(
                                            checked = exercisesViewModel.movementTypesChecked[movementType] == true,
                                            onCheckedChange = {
                                                updateState()
//
                                            }
                                        )
                                    }
                                },
                                onClick = { updateState() }
                            )
                        }
                    }
                }
            }
        }
    }
}

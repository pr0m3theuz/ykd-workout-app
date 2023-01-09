package com.example.myapplication.components.exercisesList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.components.SearchBar
import com.example.myapplication.data.ExerciseModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ExercisesScreen(
    exercisesViewModel: ExercisesViewModel
) {
    val exercises by exercisesViewModel.exercises.collectAsStateWithLifecycle(initialValue = emptyList())
    Surface {
        ExercisesLazyColumn(exercises = exercises)
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalLifecycleComposeApi::class
)
@Composable
fun ExercisesLazyColumn(
    exercisesViewModel: ExercisesViewModel = viewModel(),
    exercises: List<ExerciseModel>
) {
//    val selectedCategoriesMap by exercisesViewModel.selectedCategories.collectAsStateWithLifecycle()
//    val selectedCategoriesList = selectedCategoriesMap.keys.toList()

    fun filterExercises(search: String) {
        exercisesViewModel.filterExercises(search)
    }

    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Exercises",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(filterExercises = { search -> filterExercises(search) })

            ExercisesFilterMenu()
        }

        LazyRow(
            modifier = Modifier.align(Alignment.Start),
            horizontalArrangement = Arrangement.spacedBy(4.dp).also { Arrangement.Start },
            contentPadding = PaddingValues(0.dp)
        ) {
//            items(items = exercisesViewModel.selected.keys.toList()) { category ->
//
//                val checked = exercisesViewModel.selected[category]!!
//                fun updateState() {
//                    exercisesViewModel.onCategoryChange(category = category)
//                }
//                if (checked) {
//                    FilterChip(
//                        selected = checked,
//                        onClick = { updateState() },
//                        selectedIcon = {
//                            Icon(
//                                imageVector = Icons.Filled.Done,
//                                contentDescription = "Localized Description",
//                                modifier = Modifier.requiredSize(ChipDefaults.SelectedIconSize)
//                            )
//                        },
//                        colors = ChipDefaults.filterChipColors(
//                            backgroundColor = colors.primary,
//                            contentColor = colors.onPrimary
//                        )
//                    ) {
//                        Text(text = exercisesViewModel.getPrintableName(category = category))
//                    }
//                }

            items(items = exercisesViewModel.muscleGroupsChecked.filterValues { it }.keys.sortedBy { it.name }) { muscleGroup ->
                val checked = exercisesViewModel.muscleGroupsChecked[muscleGroup]!!
                fun updateState() {
                    exercisesViewModel.muscleGroupsChecked[muscleGroup] =
                        !exercisesViewModel.muscleGroupsChecked[muscleGroup]!!
                }
                if (checked) {
                    FilterChip(
                        selected = checked,
                        onClick = { updateState() },
                        selectedIcon = {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Localized Description",
                                modifier = Modifier.requiredSize(ChipDefaults.SelectedIconSize)
                            )
                        },
                        colors = ChipDefaults.filterChipColors(
                            backgroundColor = colors.primary,
                            contentColor = colors.onPrimary
                        )
                    ) {
                        Text(text = muscleGroup.printableName)
                    }
                }
            }
            items(items = exercisesViewModel.equipmentsChecked.filterValues { it }.keys.sortedBy { it.name }) { equipment ->
                val checked = exercisesViewModel.equipmentsChecked[equipment]!!
                fun updateState() {
                    exercisesViewModel.equipmentsChecked[equipment] =
                        !exercisesViewModel.equipmentsChecked[equipment]!!
                }
                if (checked) {
                    FilterChip(
                        selected = checked,
                        onClick = { updateState() },
                        selectedIcon = {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Localized Description",
                                modifier = Modifier.requiredSize(ChipDefaults.SelectedIconSize)
                            )
                        },
                        colors = ChipDefaults.filterChipColors(
                            backgroundColor = colors.primary,
                            contentColor = colors.onPrimary
                        )
                    ) {
                        Text(text = equipment.printableName)
                    }
                }
            }
            items(items = exercisesViewModel.movementTypesChecked.filterValues { it }.keys.sortedBy { it.name }) {
                    movementType
                ->
                val checked = exercisesViewModel.movementTypesChecked[movementType]!!
                fun updateState() {
                    exercisesViewModel.movementTypesChecked[movementType] =
                        !exercisesViewModel.movementTypesChecked[movementType]!!
                }
                if (checked) {
                    FilterChip(
                        selected = checked,
                        onClick = { updateState() },
                        selectedIcon = {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Localized Description",
                                modifier = Modifier.requiredSize(ChipDefaults.SelectedIconSize)
                            )
                        },
                        colors = ChipDefaults.filterChipColors(
                            backgroundColor = colors.primary,
                            contentColor = colors.onPrimary
                        )
                    ) {
                        Text(text = movementType.printableName)
                    }
                }
            }
        }

//        Row(modifier = Modifier.scrollable(state = filterChipRowScrollState, orientation = Orientation.Horizontal)) {
//            exercisesViewModel.muscleGroupsChecked.forEach { (muscleGroup, checked) ->
//                fun updateState() {
//                    exercisesViewModel.muscleGroupsChecked[muscleGroup] =
//                        !exercisesViewModel.muscleGroupsChecked[muscleGroup]!!
//                }
//                if (checked) {
//                    FilterChip(
//                        selected = checked,
//                        onClick = { updateState() }
// //                        colors =
//                    ) {
//                        Text(text = muscleGroup.printableName)
//                    }
//                }
//            }
//        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        contentPadding = PaddingValues(0.dp)
        ) {
            stickyHeader { /* TODO Search Function */ }
            items(count = exercises.size, key = { index: Int -> exercises[index].id }) { index ->
                val exercise: ExerciseModel = exercises[index]

                ExerciseItem(exercise = exercise)
            }
        }
    }
}

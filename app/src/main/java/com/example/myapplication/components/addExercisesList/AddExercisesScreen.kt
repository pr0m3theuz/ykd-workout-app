package com.example.myapplication.components.addExercisesList

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.exercisesList.ExerciseOptionsDropdownMenu
import com.example.myapplication.components.exercisesList.ExercisesFilterMenu
import com.example.myapplication.components.exercisesList.ExercisesViewModel
import com.example.myapplication.components.exercisesList.SelectedFiltersLazyRow
import com.example.myapplication.data.ExerciseModel

// TODO Create Advanced Search Options & Exercise Creation & Editing Screens

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AddExercisesScreen(
    setOpenDialog: (Boolean) -> Unit,
    exercisesViewModel: ExercisesViewModel = hiltViewModel(),
    workoutViewModel: WorkoutViewModel = viewModel(),
) {
    val exercises by exercisesViewModel.exercises.collectAsStateWithLifecycle(initialValue = emptyList())

    fun filterExercises(search: String) {
        exercisesViewModel.filterExercises(search)
    }

    val context = LocalContext.current
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    val exercisesSelectedToBeAddedToWorkout = remember { mutableStateListOf<ExerciseModel>() }

    fun selectExercise(exercise: ExerciseModel, isSelected: Boolean) {
        if (isSelected) {
            exercisesSelectedToBeAddedToWorkout.add(exercise)
        } else {
            exercisesSelectedToBeAddedToWorkout.remove(exercise)
        }
    }

    val dialogProperties = DialogProperties(usePlatformDefaultWidth = false)

    Dialog(
        onDismissRequest = { setOpenDialog(false) },
        properties = dialogProperties
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Add Exercises",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            //                    modifier = Modifier.align(Alignment.CenterVertically),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                setOpenDialog(false)
//                            workoutViewModel.cancelExerciseSelection()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = "Go to the previous screen"
                            )
                        }
                    },
                    actions = {
                        val iconColors =
                            IconButtonDefaults.iconButtonColors()
                        IconButton(
                            onClick = {
                                /*TODO*/
                                Toast
                                    .makeText(
                                        context,
                                        "TODO: On Click, Replace Title with Search Bar",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            },
                            enabled = true,
                            colors = iconColors
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = "Search Exercises"
                            )
                        }
                        ExercisesFilterMenu(/*modifier = Modifier.align(Alignment.CenterVertically)*/)
                        ExerciseOptionsDropdownMenu()
                    },
                )
            },
            bottomBar = { },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    workoutViewModel.addEmptyExerciseSets(exercisesSelectedToBeAddedToWorkout.toList())
                    setOpenDialog(false)
                }
                ) {
//                TODO Figure out right icon for this
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = Icons.Filled.Check,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Add Selected Exercises"
                    )
                }
            },
            content = { contentPadding ->
                Column(Modifier.padding(contentPadding)) {
                    val searchBarColors = SearchBarDefaults.colors(
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
                            filterExercises("%$it%")
                        },
                        onSearch = { active = false },
                        active = active,
                        colors = searchBarColors,
                        onActiveChange = { active = false },
                        placeholder = {
                            Text(
                                "Search Exercises",
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {}

                    SelectedFiltersLazyRow(exercisesViewModel)

                    ExercisesLazyList(
                        exercises,
                        selectExercise = { _exercise, isSelected ->
                            selectExercise(
                                _exercise,
                                isSelected
                            )
                        })
                }
            }
        )
    }

}

@Composable
private fun ExercisesLazyList(
    exercises: List<ExerciseModel>,
    selectExercise: (_exercise: ExerciseModel, selected: Boolean) -> Unit,
) {


    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                    contentPadding = PaddingValues(0.dp)
    ) {
        items(
            count = exercises.size,
            key = { index: Int -> exercises[index].lazyListItemID }
        ) { index ->
            val exercise: ExerciseModel = exercises[index]

            ToggleableExerciseItem(
                exercise = exercise,
                selectExercise = { _exercise, isSelected -> selectExercise(_exercise, isSelected) },
            )
        }
    }
}
package com.example.myapplication.components.exercisesList

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.components.BottomNavigationBar
import com.example.myapplication.data.ExerciseModel

// @OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ExercisesScreen(
    navController: NavHostController = rememberNavController(),
) {
    /*NavHost(navController = navController, startDestination = "exercisesList") {
        composable("exercisesList") {
            val viewModel = hiltViewModel<ExercisesViewModel>()
            ExercisesLazyColumn(
                navController = navController,
                exercisesViewModel = viewModel,
            )
        }
        composable(
            "exerciseDetails/{exerciseID}",
            arguments = listOf(navArgument("exerciseID") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val viewModel = hiltViewModel<ExercisesViewModel>()
            navBackStackEntry.arguments?.getString("exerciseID")?.let {
                ExerciseDetailsScreen(
                    navController,
                    exercisePK = it,
                    exercisesViewModel = viewModel,
                )
            }
        }
    }*/
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ExercisesLazyColumn(
    navController: NavController,
    exercisesViewModel: ExercisesViewModel,
) {
//    val selectedCategoriesMap by exercisesViewModel.selectedCategories.collectAsStateWithLifecycle()
//    val selectedCategoriesList = selectedCategoriesMap.keys.toList()

    val exercises by exercisesViewModel.exercises.collectAsStateWithLifecycle(initialValue = emptyList())
    Log.i("ExercisesLazyColumn.exercises", exercises.toString())
    fun filterExercises(search: String) {
        exercisesViewModel.filterExercises(search)
    }

    val context = LocalContext.current
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Exercises",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        //                    modifier = Modifier.align(Alignment.CenterVertically),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            /*TODO Navigate to Previous Screen */
                            navController.navigateUp()
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
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = { _ ->
            Column() {
                Box(modifier = Modifier.fillMaxHeight(0.075f).fillMaxWidth())
                SearchBar(
                    query = text,
                    onQueryChange = {
                        text = it
                        filterExercises("%$it%")
                    },
                    onSearch = { active = false },
                    active = active,
                    onActiveChange = { active = false },
                    placeholder = { Text("Search Exercises") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {}
                LazyRow(
                    modifier = Modifier/*.align(Alignment.Start)*/.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp).also { Arrangement.Start },
                    contentPadding = PaddingValues(0.dp)
                ) {
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
                                    backgroundColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
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
                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text(text = equipment.printableName)
                            }
                        }
                    }
                    items(items = exercisesViewModel.movementTypesChecked.filterValues { it }.keys.sortedBy { it.name }) {
                            movementType,
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
                                    backgroundColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(text = movementType.printableName)
                            }
                        }
                    }
                }

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

                        ExerciseItem(
                            exercise = exercise,
                            navController = navController
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun ExerciseOptionsDropdownMenu() {
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
                text = { androidx.compose.material3.Text("Create Exercise") },
                onClick = { /* TODO Navigate to Create Exercise Screen*/ }
            )
        }
    }
}

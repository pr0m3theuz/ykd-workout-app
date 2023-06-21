package com.example.myapplication.components.exercisesList

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.ExerciseModel

// @OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ExercisesScreen(
    exercisesViewModel: ExercisesViewModel,
) {
    val exercises by exercisesViewModel.exercises.collectAsStateWithLifecycle(initialValue = emptyList())
//    Surface {
    ExercisesLazyColumn(exercises = exercises)
//    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ExercisesLazyColumn(
    exercisesViewModel: ExercisesViewModel = viewModel(),
    exercises: List<ExerciseModel>,
) {
//    val selectedCategoriesMap by exercisesViewModel.selectedCategories.collectAsStateWithLifecycle()
//    val selectedCategoriesList = selectedCategoriesMap.keys.toList()

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
                    IconButton(onClick = { /*TODO*/ }) {
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
        content = { it ->
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
/*            items(items = exercisesViewModel.selected.keys.toList()) { category ->

                val checked = exercisesViewModel.selected[category]!!
                fun updateState() {
                    exercisesViewModel.onCategoryChange(category = category)
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
                        Text(text = exercisesViewModel.getPrintableName(category = category))
                    }
                }*/
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
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//        contentPadding = PaddingValues(0.dp)
                ) {
                    stickyHeader { /* TODO Search Function */ }
                    items(
                        count = exercises.size,
                        key = { index: Int -> exercises[index].lazyListItemID }
                    ) { index ->
                        val exercise: ExerciseModel = exercises[index]

                        ExerciseItem(exercise = exercise)
                    }
                }
            }
        }
    )
    /*    Column(
            modifier = Modifier*//*.padding(horizontal = 8.dp)*//*
//            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    *//*TODO Navigate to Previous Screen *//*
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

            Text(
                text = "Exercises",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            ExercisesFilterMenu(modifier = Modifier.align(Alignment.CenterVertically))
            ExerciseOptionsDropdownMenu()
        }

        *//*Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SearchBar(filterExercises = { search -> filterExercises(search) })
        }*//*

        var text by rememberSaveable { mutableStateOf("") }
        var active by rememberSaveable { mutableStateOf(false) }

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
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                .padding(horizontal = 8.dp)

        ) {
        }

        LazyRow(
            modifier = Modifier.align(Alignment.Start).padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp).also { Arrangement.Start },
            contentPadding = PaddingValues(0.dp)
        ) {
*//*            items(items = exercisesViewModel.selected.keys.toList()) { category ->

                val checked = exercisesViewModel.selected[category]!!
                fun updateState() {
                    exercisesViewModel.onCategoryChange(category = category)
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
                        Text(text = exercisesViewModel.getPrintableName(category = category))
                    }
                }*//*
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
            modifier = Modifier.fillMaxWidth(),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//        contentPadding = PaddingValues(0.dp)
        ) {
            stickyHeader { *//* TODO Search Function *//* }
            items(
                count = exercises.size,
                key = { index: Int -> exercises[index].lazyListItemID }
            ) { index ->
                val exercise: ExerciseModel = exercises[index]

                ExerciseItem(exercise = exercise)
            }
        }
    }*/
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

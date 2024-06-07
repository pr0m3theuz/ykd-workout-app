package com.example.myapplication.components.exercisesList

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.components.BottomNavigationBar
import com.example.myapplication.data.model.Equipment
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.MuscleGroup
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// TODO Create Advanced Search Options & Exercise Creation & Editing Screens
// @OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ExercisesScreen(navController: NavHostController = rememberNavController()) {
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
  ExperimentalMaterial3Api::class,
)
@Composable
fun ExercisesLazyColumn(
  navController: NavController,
  exercisesViewModel: ExercisesViewModel = koinViewModel(),
) {
  //    val selectedCategoriesMap by
  // exercisesViewModel.selectedCategories.collectAsStateWithLifecycle()
  //    val selectedCategoriesList = selectedCategoriesMap.keys.toList()

  val exercises by
    exercisesViewModel.exercises.collectAsStateWithLifecycle(initialValue = emptyList())

  val muscleGroups by exercisesViewModel.muscleGroup.collectAsState(initial = emptyList())

  fun filterExercises(search: String) {
    exercisesViewModel.setSearchExerciseName(search)
    exercisesViewModel.filterExercises()
  }

  val context = LocalContext.current
  var query by rememberSaveable { mutableStateOf(exercisesViewModel.searchExerciseName) }
  var active by remember { mutableStateOf(false) }
  val (searchBarVisibility, setSearchBarVisibility) = remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Exercises",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            //                    modifier = Modifier.align(Alignment.CenterVertically),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        navigationIcon = {
          IconButton(
            onClick = {
              /*TODO Navigate to Previous Screen */
              navController.navigateUp()
              Toast.makeText(context, "TODO: Navigate to Previous Screen", Toast.LENGTH_SHORT)
                .show()
            }
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen",
            )
          }
        },
        actions = {
          val iconColors = IconButtonDefaults.iconButtonColors()
          IconButton(
            onClick = { setSearchBarVisibility(!searchBarVisibility) },
            enabled = true,
            colors = iconColors,
          ) {
            Icon(
              imageVector = Icons.Outlined.Search,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Search Exercises",
            )
          }
          ExercisesFilterMenu(/*modifier = Modifier.align(Alignment.CenterVertically)*/ )
          ExerciseOptionsDropdownMenu()
        },
      )
    },
    bottomBar = { BottomNavigationBar(navController = navController) },
    content = { contentPadding ->
      Column(Modifier.padding(contentPadding)) {
        val searchBarColors =
          SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            inputFieldColors =
              TextFieldDefaults.colors(
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
              ),
          )
        /*
        AnimatedVisibility(
          visible = searchBarVisibility,
          enter = expandVertically(),
          exit = shrinkVertically()
        ) {
        */
        SearchBar(
          query = query,
          onQueryChange = {
            query = it
            filterExercises(it)
          },
          onSearch = { active = false },
          active = active,
          colors = searchBarColors,
          onActiveChange = { active = false },
          placeholder = {
            Text("Search Exercises", color = MaterialTheme.colorScheme.onSecondaryContainer)
          },
          leadingIcon = {
            Icon(
              Icons.Default.Search,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
          },
          modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        ) {}
        //        }

        SelectedFiltersLazyRow(exercisesViewModel)

        when {
          exercises.isEmpty() && query.isNotBlank() -> NoResultsFound(query = query)
          exercises.isNotEmpty() -> {
            ExercisesLazyList(exercises, muscleGroups, navController)
          }
          exercises.isEmpty() && query.isBlank() -> {
            Column(
              modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              LinearProgressIndicator(
                modifier = Modifier.semantics(mergeDescendants = true) {}.padding(10.dp)
              )
            }
          }
        }

        /*        if (exercises.isNotEmpty()) {
          ExercisesLazyList(
            exercises,
            muscleGroups,
            getMuscleGroup = { getMuscleGroups() },
            navController,
          )
        } else {
          NoResultsFound(query = query)
        }*/
      }
    },
  )
}

@Composable
fun NoResultsFound(query: String) {
  Column(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = "No exercises found for \"$query\".",
      style = MaterialTheme.typography.titleMedium,
      textAlign = TextAlign.Center,
    )
    Text(
      text = "Try refining your search criteria.",
      style = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(top = 8.dp),
    )
  }
}

@Composable
private fun ExercisesLazyList(
  exercises: List<Exercise>,
  muscleGroups: List<MuscleGroup>,
  navController: NavController,
) {
  val lazyListState = rememberLazyListState()
  // Remember a CoroutineScope to be able to launch
  val coroutineScope = rememberCoroutineScope()

  val firstVisibleItemIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
  if (firstVisibleItemIndex > 0) {
    LaunchedEffect(lazyListState) { coroutineScope.launch { lazyListState.animateScrollToItem(0) } }
  }

  LazyColumn(
    state = lazyListState,
    modifier = Modifier.fillMaxWidth(),
    //     verticalArrangement = Arrangement.spacedBy(8.dp),
    //     contentPadding = PaddingValues(8.dp)
  ) {
    items(count = exercises.size, key = { index: Int -> exercises[index].lazyListItemID }) { index
      ->
      val exercise: Exercise = exercises[index]
      val muscleGroupName: String? = muscleGroups.find { it._id == exercise.muscleGroupID }?.name
      //      Log.i(
      //        "ExercisesLazyList.exercise",
      //        "${exercise.name}, ${exercise.muscleGroupID}, $muscleGroupName"
      //      )

      ExerciseItem(
        exercise = exercise,
        muscleGroupName = muscleGroupName,
        navController = navController,
      )
    }
  }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun SelectedFiltersLazyRow(exercisesViewModel: ExercisesViewModel) {
  val muscleGroupsChecked by exercisesViewModel.muscleGroupsChecked.collectAsState()
  val equipmentsChecked by exercisesViewModel.equipmentsChecked.collectAsState()

  val labelSmall = MaterialTheme.typography.labelLarge

  val filterChipColors =
    FilterChipDefaults.filterChipColors(
      //      backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
      //      contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    )

  fun setMuscleGroupsChecked(muscleGroup: MuscleGroup, checked: Boolean) {
    exercisesViewModel.updateMuscleGroupsChecked(muscleGroup, checked)
    exercisesViewModel.filterExercises()
  }

  fun setEquipmentsChecked(equipment: Equipment, checked: Boolean) {
    exercisesViewModel.updateEquipmentsChecked(equipment, checked)
    exercisesViewModel.filterExercises()
  }

  LazyRow(
    modifier = Modifier /*.align(Alignment.Start)*/.padding(8.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp).also { Arrangement.Start },
    contentPadding = PaddingValues(0.dp),
  ) {
    items(items = muscleGroupsChecked.filterValues { it }.keys.sortedBy { it.name }) { muscleGroup
      ->
      if (muscleGroupsChecked[muscleGroup] == true) {
        FilterChip(
          selected = muscleGroupsChecked[muscleGroup] == true,
          onClick = { setMuscleGroupsChecked(muscleGroup, false) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Filled.Done,
              contentDescription = "Done",
              modifier = Modifier.size(FilterChipDefaults.IconSize),
            )
          },
          label = { Text(text = muscleGroup.name ?: "Unknown", style = labelSmall) },
          colors = filterChipColors,
        )
      }
    }

    items(items = equipmentsChecked.filterValues { it }.keys.sortedBy { it.name }) { equipment ->
      if (equipmentsChecked[equipment] == true) {
        FilterChip(
          selected = equipmentsChecked[equipment] == true,
          onClick = { setEquipmentsChecked(equipment, false) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Filled.Done,
              contentDescription = "Done",
              modifier = Modifier.size(FilterChipDefaults.IconSize),
            )
          },
          label = { Text(text = equipment.name ?: "Unknown", style = labelSmall) },
          //          colors = filterChipColors
        )
      }
    }

    items(
      items = exercisesViewModel.movementTypesChecked.filterValues { it }.keys.sortedBy { it.name }
    ) { movementType ->
      val checked = exercisesViewModel.movementTypesChecked[movementType] == true
      if (exercisesViewModel.movementTypesChecked[movementType] == true) {
        FilterChip(
          selected = checked,
          onClick = { /*TODO write setMovementChecked()*/},
          leadingIcon = {
            Icon(
              imageVector = Icons.Filled.Done,
              contentDescription = "Done",
              modifier = Modifier.size(FilterChipDefaults.IconSize),
            )
          },
          label = { Text(text = movementType.printableName, style = labelSmall) },
          //          colors = filterChipColors
        )
      }
    }
  }
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
        tint = MaterialTheme.colorScheme.onSurface,
      )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      DropdownMenuItem(
        text = { androidx.compose.material3.Text("Create Exercise") },
        onClick = { /* TODO Navigate to Create Exercise Screen*/},
      )
    }
  }
}

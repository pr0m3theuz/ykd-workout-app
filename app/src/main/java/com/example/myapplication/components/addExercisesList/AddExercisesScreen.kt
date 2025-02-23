package com.example.myapplication.components.addExercisesList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.components.exercisesList.ExerciseOptionsDropdownMenu
import com.example.myapplication.components.exercisesList.ExercisesFilterMenu
import com.example.myapplication.components.exercisesList.ExercisesViewModel
import com.example.myapplication.components.exercisesList.SelectedFiltersLazyRow
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.MuscleGroup
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// TODO Create Advanced Search Options & Exercise Creation & Editing Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExercisesScreen(
  setOpenDialog: (Boolean) -> Unit,
  exercisesViewModel: ExercisesViewModel = koinViewModel(),
  workoutViewModel: WorkoutViewModel = koinViewModel(),
  navController: NavController,
  navigateToDetailPane: (exerciseID: String) -> Unit,
) {
  val exercises by
  exercisesViewModel.exercises.collectAsStateWithLifecycle(initialValue = emptyList())

  val muscleGroups by exercisesViewModel.muscleGroup.collectAsState(initial = emptyList())

  fun getMuscleGroups() {
    /*TODO*/
  }

  fun filterExercises(search: String) {
    exercisesViewModel.setSearchExerciseName(search)
    exercisesViewModel.filterExercises()
  }

  var query by rememberSaveable { mutableStateOf(exercisesViewModel.searchExerciseName) }
  var active by remember { mutableStateOf(false) }
  val (searchBarVisibility, setSearchBarVisibility) = remember { mutableStateOf(false) }

  val exercisesSelectedToBeAddedToWorkout = remember { mutableSetOf<Exercise>() }

  fun selectExercise(exercise: Exercise, isSelected: Boolean) {
    if (isSelected) {
      exercisesSelectedToBeAddedToWorkout.add(exercise)
    } else {
      exercisesSelectedToBeAddedToWorkout.remove(exercise)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Add Exercises",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            // modifier = Modifier.align(Alignment.CenterVertically),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        navigationIcon = {
          /*IconButton(
            onClick = {
              setOpenDialog(false)
              // workoutViewModel.cancelExerciseSelection()
            }
          ) {
            Icon(
              imageVector = Icons.Outlined.Close,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen",
            )
          }*/
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
          ExercisesFilterMenu(/*modifier = Modifier.align(Alignment.CenterVertically)*/)
          ExerciseOptionsDropdownMenu()
        },
      )
    },
    bottomBar = {},
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          workoutViewModel.addMultipleEmptyExerciseSets(
            exercisesSelectedToBeAddedToWorkout.toList()
          )
          setOpenDialog(false)
//          navController.navigateUp()
        }
      ) {
        // TODO Figure out right icon for this
        Icon(
          modifier = Modifier.size(24.dp),
          imageVector = Icons.Filled.Check,
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = "Add Selected Exercises",
        )
      }
    },
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
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        ) {}

        SelectedFiltersLazyRow(exercisesViewModel)

        when {
          exercises.isEmpty() && query.isNotBlank() -> NoResultsFound(query = query)
          exercises.isNotEmpty() -> {
            ExercisesLazyList(
              exercises = exercises,
              muscleGroups = muscleGroups,
//              selectExercise = { _exercise, isSelected -> selectExercise(_exercise, isSelected) },
//              navController = navController,
            ) { exercise, muscleGroupName ->
              ToggleableExerciseItem(
                exercise = exercise,
                muscleGroupName = muscleGroupName,
                navController = navController,
                selectExercise = { _exercise, isSelected -> selectExercise(_exercise, isSelected) },
                navigateToDetailPane = { exerciseID: String -> navigateToDetailPane(exerciseID) },
              )
            }

          }

          exercises.isEmpty() && query.isBlank() -> {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              LinearProgressIndicator(
                modifier = Modifier
                  .semantics(mergeDescendants = true) {}
                  .padding(10.dp)
              )
            }
          }
        }
      }
    },
  )
}

@Composable
fun NoResultsFound(query: String) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
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
//  selectExercise: (_exercise: Exercise, selected: Boolean) -> Unit,
//  navController: NavController,
  exerciseItem: @Composable (exercise: Exercise, muscleGroupName: String?) -> Unit
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
    // verticalArrangement = Arrangement.spacedBy(8.dp),
    // contentPadding = PaddingValues(0.dp)
  ) {
    items(count = exercises.size, key = { index: Int -> exercises[index].lazyListItemID }) { index
      ->
      val exercise: Exercise = exercises[index]
      val muscleGroupName: String? = muscleGroups.find { it.id == exercise.muscleGroupID }?.name

//      ToggleableExerciseItem(
//        exercise = exercise,
//        muscleGroupName = muscleGroupName,
//        navController = navController,
//        selectExercise = { _exercise, isSelected -> selectExercise(_exercise, isSelected) },
//        navigateToDetailPane = { exerciseID: String -> navigateToDetailPane(exerciseID) },
//
//      )

      exerciseItem(exercise, muscleGroupName)
    }
  }
}

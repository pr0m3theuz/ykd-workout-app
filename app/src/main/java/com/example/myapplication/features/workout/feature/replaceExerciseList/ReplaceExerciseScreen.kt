package com.example.myapplication.features.addExercisesList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.navigation.NavController
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.MuscleGroup
import com.example.myapplication.features.exercises.ExercisesViewModel
import com.example.myapplication.features.exercises.ui.ExerciseItem
import com.example.myapplication.features.exercises.ui.ExerciseOptionsDropdownMenu
import com.example.myapplication.features.exercises.ui.ExerciseSearchBar
import com.example.myapplication.features.exercises.ui.ExercisesFilterMenu
import com.example.myapplication.features.exercises.ui.SelectedFiltersLazyRow
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.addExercisesList.NoResultsFound
import com.example.myapplication.features.workout.feature.replaceExerciseList.SelectableExerciseItem
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.koin.androidx.compose.koinViewModel

// TODO Create Advanced Search Options & Exercise Creation & Editing Screens

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReplaceExerciseScreen(
  exerciseSetsID: ObjectId,
  setOpenDialog: (Boolean) -> Unit,
  exercisesViewModel: ExercisesViewModel = koinViewModel(),
  workoutViewModel: WorkoutViewModel,
  navController: NavController,
  navigateToDetailPane: (exerciseID: String) -> Unit,
  onNavigateBack: () -> Unit,
) {
  val uiState by exercisesViewModel.uiState.collectAsState()

  val exerciseSets = workoutViewModel.getExerciseSetsByID(exerciseSetsID)

  val exercises = uiState.exercises

  val muscleGroupsChecked = uiState.muscleGroupsChecked
  val equipmentsChecked = uiState.equipmentsChecked
  val movementTypesChecked = exercisesViewModel.movementTypesChecked

  val musclesChecked = uiState.selectedMuscleGroups
  val selectedEquipment = uiState.selectedEquipments
  val selectedMovementTypes = uiState.selectedMovementTypes

  val muscleGroups = uiState.muscleGroups

  fun filterExercises(search: String) {
    exercisesViewModel.setSearchExerciseName(search)
    exercisesViewModel.filterExercises()
  }

  var query by rememberSaveable { mutableStateOf(exercisesViewModel.searchExerciseName) }
  var active by remember { mutableStateOf(false) }
  val (searchBarVisibility, setSearchBarVisibility) = remember { mutableStateOf(false) }

  val exerciseSelectedToBeAddedToWorkout = remember { mutableStateListOf<Exercise>() }

  fun selectExercise(exercise: Exercise, isSelected: Boolean) {
    if (isSelected && exerciseSelectedToBeAddedToWorkout.size == 1) {
      exerciseSelectedToBeAddedToWorkout[0] = exercise
    } else if (isSelected && exerciseSelectedToBeAddedToWorkout.isEmpty()) {
      exerciseSelectedToBeAddedToWorkout.add(exercise)
    }
  }

  Scaffold(
    modifier = Modifier.imePadding(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Replace Exercise",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            // modifier = Modifier.align(Alignment.CenterVertically),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        navigationIcon = {
//          IconButton(
//            onClick = {
//              setOpenDialog(false)
//              // workoutViewModel.cancelExerciseSelection()
//            }
//          ) {
//            Icon(
//              imageVector = Icons.Outlined.Close,
//              tint = MaterialTheme.colorScheme.onSurface,
//              contentDescription = "Go to the previous screen",
//            )
//          }
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
          if (exerciseSelectedToBeAddedToWorkout.isNotEmpty() && exerciseSets != null) {
            workoutViewModel.replaceExerciseInExerciseSets(
              exerciseSets = exerciseSets,
              newExercise = exerciseSelectedToBeAddedToWorkout.first(),
            )
          }
          onNavigateBack()
//          setOpenDialog(false)
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
      Column(
        Modifier
          .padding(contentPadding)
          .fillMaxWidth()
      ) {
        val searchBarColors =
          SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
//            inputFieldColors =
//              TextFieldDefaults.colors(
//                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
//              ),
          )

        if (searchBarVisibility) {
/*          val onActiveChange = { _active: Boolean -> active = _active }
          SearchBar(
            windowInsets = WindowInsets(0.dp),
            inputField = {
              SearchBarDefaults.InputField(
                query = query,
                onQueryChange = {
                  query = it
                  filterExercises(it)
                },
                onSearch = { active = false },
                expanded = false,
                onExpandedChange = onActiveChange,
                enabled = true,
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
                trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null) },
                interactionSource = null,
              )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp),
            shape = SearchBarDefaults.inputFieldShape,
            colors = searchBarColors,
          ) {}*/
          ExerciseSearchBar(
            query = query,
            onQueryChange = {
              query = it
              filterExercises(it)
            },
            onSearch = {},
            searchResults = exercises,
            onDismissRequest = { setSearchBarVisibility(false) },
            muscleGroups = muscleGroupsChecked,
            equipment = equipmentsChecked,
            movementTypes = movementTypesChecked,
            onMuscleGroupsSelected = { muscleGroup, checked -> },
            onEquipmentSelected = { equipment, checked -> },
            onMovementTypeSelected = { movementType, checked -> },
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
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
              )
            },
            selectedFiltersLazyRow = {
              val visible =
                muscleGroupsChecked.isNotEmpty() ||
                        selectedEquipment.isNotEmpty() ||
                        selectedMovementTypes.isNotEmpty()
              AnimatedVisibility(visible) {
                if (
                  musclesChecked.isNotEmpty() ||
                  selectedEquipment.isNotEmpty() ||
                  selectedMovementTypes.isNotEmpty()
                ) {
                  SelectedFiltersLazyRow(
                    exercisesViewModel,
                    muscleGroupsChecked,
                    equipmentsChecked,
                    movementTypesChecked,
                    musclesChecked,
                    selectedEquipment,
                    selectedMovementTypes,
                  )
                }
              }
            },
            exerciseItem = {},
            exercisesLazyList = {
              ExercisesLazyList(
                exercises = exercises,
                muscleGroups = muscleGroups,
                uiState.allExercisesSetsPerformed,
                selectedExercise = exerciseSelectedToBeAddedToWorkout.firstOrNull(),
              ) { exercise, muscleGroupName, setsPerformedCount, isSelectedExercise ->
                SelectableExerciseItem(
                  exercise = exercise,
                  muscleGroupName = muscleGroupName,
                  selectedExercise = isSelectedExercise,
                  setsPerformedCount = setsPerformedCount,
                  selectExercise = { _exercise, isSelected -> selectExercise(_exercise, isSelected) },
                  navigateToDetailPane = { exerciseID: String -> navigateToDetailPane(exerciseID) },
                )
              }
            }
          )
        }


        when {
          exercises.isEmpty() && query.isNotBlank() -> NoResultsFound(query = query)
          exercises.isNotEmpty() -> {
            ExercisesLazyList(
              exercises = exercises,
              muscleGroups = muscleGroups,
              uiState.allExercisesSetsPerformed,
              selectedExercise = exerciseSelectedToBeAddedToWorkout.firstOrNull(),
            ) { exercise, muscleGroupName, setsPerformedCount, isSelectedExercise ->
              SelectableExerciseItem(
                exercise = exercise,
                muscleGroupName = muscleGroupName,
                selectedExercise = isSelectedExercise,
                setsPerformedCount = setsPerformedCount,
                selectExercise = { _exercise, isSelected -> selectExercise(_exercise, isSelected) },
                navigateToDetailPane = { exerciseID: String -> navigateToDetailPane(exerciseID) },
              )
            }
            //                navController = navController,
          }

          query.isBlank() -> {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              CircularProgressIndicator(
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
private fun ExercisesLazyList(
  exercises: List<Exercise>,
  muscleGroups: List<MuscleGroup>,
  exercisesSetsPerformedCount: Map<ObjectId, Int>,
  selectedExercise: Exercise?,
  //  selectExercise: (_exercise: Exercise, selected: Boolean) -> Unit,
  //  navController: NavController,
  exerciseItem:
  @Composable
    (exercise: Exercise, muscleGroupName: String?, setsPerformedCount: Int?, isSelectedExercise: Boolean) -> Unit,
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
    modifier = Modifier
      .fillMaxWidth()
      .selectableGroup(),
    // verticalArrangement = Arrangement.spacedBy(8.dp),
    // contentPadding = PaddingValues(0.dp)
  ) {
    items(count = exercises.size, key = { index: Int -> exercises[index].lazyListItemID }) { index
      ->
      var isSelectedExercise by remember { mutableStateOf(false) }
      val exercise: Exercise = exercises[index]
      val muscleGroupName: String? = muscleGroups.find { it.id == exercise.muscleGroupID }?.name
      isSelectedExercise = selectedExercise == exercise
      //      SelectableExerciseItem(
      //        exercise = exercise,
      //        selectedExercise = selectedExerciseModel,
      //        muscleGroupName = muscleGroupName,
      //        navController = navController,
      //        selectExercise = { _exercise, isSelected -> selectExercise(_exercise, isSelected) },
      //      )
      exerciseItem(
        exercise,
        muscleGroupName,
        exercisesSetsPerformedCount[exercise.id],
        isSelectedExercise
      )
    }
  }
}

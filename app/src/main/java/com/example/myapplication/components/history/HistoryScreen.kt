package com.example.myapplication.components.history

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.myapplication.components.BottomNavigationBar
import com.example.myapplication.components.exercise.AddSet
import com.example.myapplication.components.exercise.ExerciseScreen
import com.example.myapplication.components.exercise.SetContent
import com.example.myapplication.components.exercise.SetLazyListItem
import com.example.myapplication.components.workout.AddExercise
import com.example.myapplication.components.workout.ExerciseSetLazyColumn
import com.example.myapplication.components.workout.SwipeToDismissWorkoutNote
import com.example.myapplication.components.workout.WorkoutSummaryCard
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import org.mongodb.kbson.ObjectId
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
)
@Composable
fun HistoryScreen(
  navController: NavController,
) {
  //    val selectedCategoriesMap by
  // exercisesViewModel.selectedCategories.collectAsStateWithLifecycle()
  //    val selectedCategoriesList = selectedCategoriesMap.keys.toList()

  fun filterWorkouts(search: String) {
    // TODO .setSearchExerciseName(search)
    // TODO .filterExercises()
  }

  val context = LocalContext.current
  //  var query by rememberSaveable { mutableStateOf(/* TODO .searchExerciseName */ ) }
  var active by remember { mutableStateOf(false) }
  val (searchBarVisibility, setSearchBarVisibility) = remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "History",
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
              /*TODO Navigate to Previous Screen */
              navController.navigateUp()
              Toast.makeText(context, "TODO: Navigate to Previous Screen", Toast.LENGTH_SHORT)
                .show()
            },
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen"
            )
          }
        },
        actions = {
          val iconColors = IconButtonDefaults.iconButtonColors()
          IconButton(
            onClick = { setSearchBarVisibility(!searchBarVisibility) },
            enabled = true,
            colors = iconColors
          ) {
            Icon(
              imageVector = Icons.Outlined.Search,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Search Exercises"
            )
          }
          //          TODO ExercisesFilterMenu(/*modifier =
          // Modifier.align(Alignment.CenterVertically)*/ )
          //          TODO ExerciseOptionsDropdownMenu()
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
              unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary
            )
          )
        /*
        AnimatedVisibility(
          visible = searchBarVisibility,
          enter = expandVertically(),
          exit = shrinkVertically()
        ) {
        */
        SearchBar(
          query = "TODO" /* TODO query */,
          onQueryChange = {
            //            query = it
            //            filterExercises(it)
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
              tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
        ) {}
        //        }

        /*SelectedFiltersLazyRow(exercisesViewModel)

         when {
           exercises.isEmpty() && query.isNotBlank() -> NoResultsFound(query = query)
           exercises.isNotEmpty() -> {
             ExercisesLazyList(
               exercises,
               muscleGroups,
               navController,
             )
           }
           exercises.isEmpty() && query.isBlank() -> {
             Column(
               modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
               horizontalAlignment = Alignment.CenterHorizontally
             ) {
               LinearProgressIndicator(
                 modifier = Modifier.semantics(mergeDescendants = true) {}.padding(10.dp),
               )
        0     }
           }
         }*/

        HistoryLazyColumn(navController = navController)
      }
    }
  )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryLazyColumn(
  historyViewModel: HistoryViewModel = koinViewModel(),
  navController: NavController
) {

  val workouts by historyViewModel.workouts.collectAsState()

  val groupedWorkouts =
    workouts.groupBy {
      Instant.fromEpochSeconds(it.workoutEnded?.epochSeconds!!)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .month
    }

  Log.i("HistoryLazyColumn", "workout count: ${workouts.size}")

  LazyColumn(
    contentPadding = PaddingValues(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    groupedWorkouts.forEach() { month, monthWorkouts ->
      stickyHeader {
        Text(
          modifier = Modifier.padding(0.dp),
          text = month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        )
      }

      // TODO Implement Search & Filtering of Workouts & Programs
      // items(count = workouts.size, key = { index: Int -> workouts[index].lazyListItemID }) {
      // index,
      items(monthWorkouts) { workout ->
        Box(
          modifier =
          Modifier.clickable(
            onClick = {
              // TODO Navigate to Workout screen
              navController.navigate("history?workoutID=${workout._id.toHexString()}")
            },
            enabled = true,
            interactionSource = remember { MutableInteractionSource() },
            indication = LocalIndication.current,
          )
        ) {
          WorkoutSummaryCard(
            completedWorkout = workout, // s[index],
            userDefaultWeightUnit = historyViewModel.userDefaultWeightUnit,
            getExercise = { exerciseID -> historyViewModel.getExerciseByExerciseID(exerciseID) }
          )
        }
      }
    }
  }
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WorkoutDialog(
  historyViewModel: HistoryViewModel = koinViewModel(),
  workoutID: String,
  //  getExercise: (exerciseID: ObjectId) -> Exercise?,
  navController: NavController,
  //  exerciseExerciseSets: List<ExerciseExerciseSets>
) {
  //  TODO Create UI Element to enable the user to be able to save any the changes made to the
  //  TODO Create UI Element to enable the user to be able to cancel any changes made to the workout

  val workout = historyViewModel.getWorkoutByID(ObjectId(workoutID).asObjectId())

  val dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
  workout?.let {
    // Dialog(onDismissRequest = { /*TODO*/}, properties = dialogProperties) {}

    val exerciseExerciseSets = historyViewModel.loadExercisesSetsFromWorkout(workout.exercisesSets)

    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            workout.name?.let { BasicTextField2(value = it, onValueChange = { workout.name = it }) }
          },
          navigationIcon = {
            IconButton(
              onClick = { navController.navigateUp() },
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Go to the previous screen"
              )
            }
          },
          actions = {},
        )
      },
      bottomBar = { BottomNavigationBar(navController = navController) },
    ) { contentPadding ->
      Column(Modifier.padding(contentPadding)) {
        Row(
          modifier = Modifier
            .padding(start = 8.dp)
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {}

        ExerciseSetLazyColumn(
          exercises = exerciseExerciseSets,
          workoutNotes = workout.notes.toList(),

          enableCancelWorkout = true,
          WorkoutNoteItem = { workoutNote ->
            SwipeToDismissWorkoutNote(
              workoutNote,
              removeWorkoutNote = { /*historyViewModel.removeWorkoutNote(workoutNote)*/ },
            )
          },
          ExerciseItem = { exerciseSets ->
            ExerciseScreen(
              navController,
              _exerciseSets = exerciseSets,
            )
          },
          SetItem = { exerciseSets, set, index ->
            val setUUID = set.uuid
            SetLazyListItem(
              exerciseSets = exerciseSets,
              set = set,
              index = index,
              removeSet = { /*historyViewModel.removeSet(exerciseSets, set)*/ },
              setContent = {
                SetContent(
                  exerciseSets,
                  index,
                  set,
                  getWeightUnit = { historyViewModel.getWeightUnit(exerciseSets) },
                  populatePreviousSetField = {
                    exerciseSets.exercise?.let { exercise ->
                      set.setNumber?.let { setNumber ->
                        historyViewModel.populatePreviousSetField(
                          exercise._id,
                          setNumber = setNumber
                        )
                      }
                    }
                  },
                  updateSetReps = { reps ->
                    historyViewModel.updateSetReps(
                      exerciseSets = exerciseSets,
                      setUUID = setUUID,
                      newReps = reps.toIntOrNull()
                    )
                  },
                  updateSetRIR = { rir ->
                    historyViewModel.updateSetRIR(
                      exerciseSets = exerciseSets,
                      setUUID = setUUID,
                      newRIR = rir.toDoubleOrNull(),
                    )
                  },
                  updateSetWeight = { weight ->
                    historyViewModel.updateSetWeight(
                      exerciseSets = exerciseSets,
                      setUUID = setUUID,
                      newWeight = weight.toDoubleOrNull()
                    )
                  },
                  isTimerRunning = false,
                  updateSetType = { setTypeID ->
                    historyViewModel.updateSetType(
                      exerciseSets = exerciseSets,
                      setUUID = setUUID,
                      setTypeID = setTypeID
                    )
                  },
                  setComplete = { setComplete ->
                    historyViewModel.setComplete(
                      exerciseSets = exerciseSets,
                      setUUID = setUUID,
                      setComplete = setComplete
                    )
                  },
                  startSetCompletedTimer = {},
                )
              }
            )
          },
          AddSet = { exerciseSets ->
            AddSet(addSet = { /*historyViewModel.addSet(exerciseSets)*/ })
          },
          AddExercise = { AddExercise(navController = navController) },
          CancelWorkout = {},
        )
      }
    }
  }
}

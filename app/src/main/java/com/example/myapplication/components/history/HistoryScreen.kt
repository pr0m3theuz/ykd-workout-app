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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.myapplication.R
import com.example.myapplication.components.BottomNavigationBar
import com.example.myapplication.components.EditWorkoutOptionsDropdownMenu
import com.example.myapplication.components.WorkoutOptionsDropdownMenu
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.components.exercise.AddSet
import com.example.myapplication.components.exercise.ExerciseScreen
import com.example.myapplication.components.exercise.SetContent
import com.example.myapplication.components.exercise.SetLazyListItem
import com.example.myapplication.components.workout.AddExercise
import com.example.myapplication.components.workout.ExerciseSetLazyColumn
import com.example.myapplication.components.workout.SwipeToDismissWorkoutNote
import com.example.myapplication.components.workout.WorkoutSummaryCard
import com.example.myapplication.data.model.formatLongWorkoutStarted
import com.example.myapplication.data.model.formatWorkoutDuration
import com.example.myapplication.data.model.setTextRIR
import com.example.myapplication.utils.doubleFormatted
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import org.mongodb.kbson.ObjectId
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
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
              tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
          },
          modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
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
    },
  )
}

@Composable
fun NoWorkoutsPerformed() {
  Column(
    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    //    LinearProgressIndicator(
    //      modifier = Modifier.semantics(mergeDescendants = true) {}.padding(10.dp),
    //    )
    Column(
      modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text("No Workouts Performed!")
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryLazyColumn(
  historyViewModel: HistoryViewModel = koinViewModel(),
  navController: NavController,
) {

  val workouts by historyViewModel.workouts.collectAsState()

  val groupedWorkouts =
    workouts.groupBy {
      Instant.fromEpochSeconds(it.workoutEnded?.epochSeconds!!)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .month
    }

  Log.i("HistoryLazyColumn", "workout count: ${workouts.size}")

  if (workouts.isEmpty()) {
    NoWorkoutsPerformed()
  } else {
    LazyColumn(
      contentPadding = PaddingValues(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      groupedWorkouts.forEach() { month, monthWorkouts ->
        stickyHeader {
          Text(
            modifier = Modifier.padding(0.dp),
            text = month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
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
                  navController.navigate("history?overviewWorkout=${workout._id.toHexString()}")
                },
                enabled = true,
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
              )
          ) {
            WorkoutSummaryCard(
              completedWorkout = workout,
              userDefaultWeightUnit = historyViewModel.userDefaultWeightUnit,
              getExercise = { exerciseID -> historyViewModel.getExerciseByExerciseID(exerciseID) },
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutOverview(
  historyViewModel: HistoryViewModel = koinViewModel(),
  workoutID: String,
  //  getExercise: (exerciseID: ObjectId) -> Exercise?,
  navController: NavController,
) {
  val userDefaultWeightUnit = historyViewModel.userDefaultWeightUnit

  historyViewModel.getWorkoutByID(org.mongodb.kbson.BsonObjectId(workoutID).asObjectId())
  val workout by historyViewModel.workout.collectAsState()

  workout?.exercisesSets?.let { historyViewModel.loadExercisesSetsFromWorkout(it) }
  val exerciseExerciseSets by historyViewModel.exercisesSets.collectAsState()

  val (openDeleteWorkoutDialog, setOpenDeleteWorkoutDialog) = remember { mutableStateOf(false) }

  if (openDeleteWorkoutDialog) {
    workout?.let { itWorkout ->
      ConfirmDeleteWorkout(
        onDismissRequest = { setOpenDeleteWorkoutDialog(false) },
        deleteWorkout = { historyViewModel.deleteWorkout(itWorkout) },
      )
    }
  }

  when (workout) {
    null -> NoWorkoutFound()
    else ->
      workout?.let { itWorkout ->
        Scaffold(
          topBar = {
            TopAppBar(
              title = {
                itWorkout.name?.let { workoutName ->
                  Text(text = workoutName, style = MaterialTheme.typography.titleLarge)
                }
              },
              navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = "Go to the previous screen",
                  )
                }
              },
              actions = {
                EditWorkoutOptionsDropdownMenu(
                  editWorkout = {
                    navController.navigate("history?editWorkout=${itWorkout._id.toHexString()}")
                  },
                  deleteWorkout = { setOpenDeleteWorkoutDialog(true) },
                  saveWorkoutAsTemplate = { /* TODO Create Save Workout As Template Function */},
                  //              workoutNameDialog = {}
                )
              },
            )
          },
          bottomBar = {
            Button(
              modifier = Modifier.fillMaxWidth().padding(12.dp),
              onClick = {
                navController.navigate("workout?workoutID=${itWorkout._id.toHexString()}") {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  // Avoid multiple copies of the same destination when
                  // reselecting the same item
                  launchSingleTop = true
                  // Restore state when reselecting a previously selected item
                  restoreState = true
                }
              },
            ) {
              Text(text = "Perform Again")
            }
            //        BottomNavigationBar(navController = navController)
          },
        ) { contentPadding ->
          Column(
            Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Column(Modifier.padding(horizontal = 12.dp)) {
              Row(
                modifier = Modifier.padding().fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
                  Text(itWorkout.formatLongWorkoutStarted())
                }
              }
              Row(
                modifier = Modifier.padding().fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                  ) {
                    Icon(Icons.Filled.Timer, contentDescription = null)
                    Text(text = itWorkout.formatWorkoutDuration())
                  }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                  ) {
                    val weightIcon = painterResource(R.drawable.weight_fill1_wght400_grad0_opsz24)
                    Icon(painter = weightIcon, contentDescription = null)
                    Text(
                      text =
                        "${
                        doubleFormatted(
                          when (userDefaultWeightUnit) {
                            "kg" -> itWorkout.totalVolumeInKilograms
                            else -> itWorkout.totalVolumeInPounds
                          }
                        )
                      } $userDefaultWeightUnit"
                    )
                  }
                }
              }

              // TODO Workout Notes
              Box {
                Column {
                  itWorkout.notes.forEach { workoutNote ->
                    Row { Text(text = workoutNote.note ?: "") }
                  }
                }
              }

              exerciseExerciseSets.forEach { exerciseSets ->
                Box(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth()) {
                  Column(modifier = Modifier.fillMaxWidth()) {
                    Row {
                      Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = exerciseSets.exercise?.name ?: "Exercise Name",
                        style = MaterialTheme.typography.titleMedium,
                      )
                    }
                    exerciseSets.sets.forEach { set ->
                      // TODO Chanage Text Color based on Set Type
                      Row {
                        Text(
                          text = "${set.setNumber}   ${set.setTextRIR(userDefaultWeightUnit)}",
                          style = MaterialTheme.typography.bodyMedium,
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
  }
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EditWorkoutScreen(
  workoutViewModel: WorkoutViewModel = koinViewModel(),
  workoutID: String,
  //  getExercise: (exerciseID: ObjectId) -> Exercise?,
  navController: NavController,
  //  exerciseExerciseSets: List<ExerciseExerciseSets>
) {
  val userDefaultWeightUnit = workoutViewModel.userDefaultWeightUnit

  if (!workoutID.isNullOrBlank()) {
    workoutViewModel.loadWorkoutFromPreviousWorkout(ObjectId.invoke(workoutID))
  } else {
    NoWorkoutFound()
  }

  val workout by workoutViewModel.workout.collectAsState(null)
  val exerciseExerciseSets by workoutViewModel.exercisesSets.collectAsState()
  val workoutNotes by workoutViewModel.workoutNotes.collectAsState()
  val workoutName by workoutViewModel.workoutName.collectAsState()

  val (openSaveChangesDialog, setOpenSaveChangesDialog) = remember { mutableStateOf(false) }

  if (openSaveChangesDialog) {
    ConfirmChangesToWorkout(
      onDismissRequest = { setOpenSaveChangesDialog(false) },
      saveWorkout = { workoutViewModel.saveWorkout() },
    )
  }

  workout?.let { itWorkout ->
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            workoutName?.let { workoutName ->
              BasicTextField(
                value = workoutName,
                textStyle = MaterialTheme.typography.titleLarge,
                onValueChange = { newName -> workoutViewModel.setWorkoutName(newName) },
              )
            }
          },
          navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
              Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Go to the previous screen",
              )
            }
          },
          actions = {
            WorkoutOptionsDropdownMenu(
              addWorkoutNote = { workoutViewModel.addWorkoutNote() },
              enableWorkoutNameTextfield = {},
              workoutNameDialog = {},
            )
          },
        )
      },
      floatingActionButton = {
        FloatingActionButton(onClick = { setOpenSaveChangesDialog(true) }) {
          Icon(imageVector = Icons.Filled.Save, contentDescription = "Save Changes")
        }
      },
      bottomBar = { /*BottomNavigationBar(navController = navController) */},
    ) { contentPadding ->
      Column(Modifier.padding(contentPadding), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
            Text(itWorkout.formatLongWorkoutStarted())
          }
        }
        Row(
          modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(Icons.Filled.Timer, contentDescription = null)
              Text(text = itWorkout.formatWorkoutDuration())
            }
          }
        }

        if (exerciseExerciseSets.isNotEmpty()) {
          ExerciseSetLazyColumn(
            exercises = exerciseExerciseSets,
            workoutNotes = workoutNotes,
            enableCancelWorkout = true,
            WorkoutNoteItem = { workoutNote ->
              SwipeToDismissWorkoutNote(
                workoutNote,
                removeWorkoutNote = { workoutViewModel.removeWorkoutNote(workoutNote) },
              )
            },
            ExerciseItem = { exerciseSets ->
              ExerciseScreen(
                navController,
                _exerciseSets = exerciseSets,
                weightUnit = workoutViewModel.getWeightUnit(exerciseSets),
                exerciseWeightUnit = exerciseSets.exercise?.weightUnit,
                userDefaultWeightUnit = workoutViewModel.userDefaultWeightUnit,
                removeExerciseSets = { workoutViewModel.removeExerciseSets(exerciseSets) },
                addExerciseNote = {
                  workoutViewModel.addExerciseNote(exerciseSets /*, itWorkout._id*/)
                },
                removeExerciseNote = { note ->
                  workoutViewModel.removeExerciseNote(exerciseSets, note)
                },
                pinExerciseNote = { note, pinned ->
                  workoutViewModel.pinExerciseNote(exerciseSets, note, pinned)
                },
                updateExerciseWeightUnit = {
                  workoutViewModel.updateExerciseWeightUnit(exerciseSets.exercise, it)
                },
              )
            },
            SetItem = { exerciseSets, set, index ->
              val setUUID = set.uuid
              SetLazyListItem(
                exerciseSets = exerciseSets,
                set = set,
                index = index,
                removeSet = { workoutViewModel.removeSet(exerciseSets, set) },
                setContent = {
                  SetContent(
                    exerciseSets,
                    index,
                    set,
                    getWeightUnit = { workoutViewModel.getWeightUnit(exerciseSets) },
                    populatePreviousSetField = {
                      exerciseSets.exercise?.let { exercise ->
                        set.setNumber?.let { setNumber ->
                          workoutViewModel.populatePreviousSetField(
                            exercise._id,
                            setNumber = setNumber,
                          )
                        }
                      }
                    },
                    updateSetReps = { reps ->
                      workoutViewModel.updateSetReps(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        newReps = reps.toIntOrNull(),
                      )
                    },
                    updateSetRIR = { rir ->
                      workoutViewModel.updateSetRIR(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        newRIR = rir.toDoubleOrNull(),
                      )
                    },
                    updateSetWeight = { weight ->
                      workoutViewModel.updateSetWeight(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        newWeight = weight.toDoubleOrNull(),
                      )
                    },
                    isTimerRunning = false,
                    updateSetType = { setTypeID ->
                      workoutViewModel.updateSetType(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        setTypeID = setTypeID,
                      )
                    },
                    setComplete = { setComplete ->
                      workoutViewModel.completeSet(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        completed = setComplete,
                      )
                    },
                    startSetCompletedTimer = {},
                  )
                },
              )
            },
            AddSet = { exerciseSets -> AddSet(addSet = { workoutViewModel.addSet(exerciseSets) }) },
            AddExercise = { AddExercise(navController = navController) },
            CancelWorkout = {},
          )
        }
      }
    }
  }
}

@Composable
fun ConfirmChangesToWorkout(onDismissRequest: () -> Unit, saveWorkout: () -> Unit) {
  AlertDialog(
    onDismissRequest = { onDismissRequest() },
    icon = { Icon(imageVector = Icons.Filled.Warning, contentDescription = null) },
    title = { Text(text = "Save Changes To Workout") },
    text = { Text("Are you sure you want to save the changes to this workout?") },
    confirmButton = {
      TextButton(
        onClick = {
          onDismissRequest()
          saveWorkout()
        }
      ) {
        Text("Save")
      }
    },
    dismissButton = { TextButton(onClick = { onDismissRequest() }) { Text("Cancel") } },
  )
}

@Composable
fun ConfirmDeleteWorkout(onDismissRequest: () -> Unit, deleteWorkout: () -> Unit) {
  AlertDialog(
    onDismissRequest = { onDismissRequest() },
    icon = { Icon(imageVector = Icons.Filled.Warning, contentDescription = null) },
    title = { Text(text = "Confirm Delete Workout") },
    text = { Text("Are you sure you want to delete this workout?") },
    confirmButton = {
      TextButton(
        onClick = {
          onDismissRequest()
          deleteWorkout()
        }
      ) {
        Text("Delete")
      }
    },
    dismissButton = { TextButton(onClick = { onDismissRequest() }) { Text("Cancel") } },
  )
}

@Composable
fun NoWorkoutFound() {
  Column(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(imageVector = Icons.Filled.Error, contentDescription = null)
    Text(text = "No Workout Found!")
  }
}

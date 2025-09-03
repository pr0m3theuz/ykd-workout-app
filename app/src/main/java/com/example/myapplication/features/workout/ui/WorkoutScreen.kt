

package com.example.myapplication.features.workout.ui

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.example.myapplication.common.ui.component.BottomNavigationBar
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.exercise.ExercisesSetsScreen
import com.example.myapplication.features.workout.feature.setRestTimer.RestTimerButton
import kotlinx.coroutines.launch

// TODO Takes a Workout Object

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun WorkoutScreen(
  navController: NavController,
  workoutViewModel: WorkoutViewModel,
  workoutID: String?,
  loadPreviousWorkout: Boolean,
  copyWorkoutFromPreviousWorkout: Boolean,
  currentDestination: NavDestination? = null,
  onNavigateBottomBar: (route: String) -> Unit,
) {
//  LaunchedEffect(workoutID) {
//    if (!workoutID.isNullOrBlank() && !copyWorkoutFromPreviousWorkout) {
//      Log.d("WorkoutScreen", "workoutID: $workoutID, calling getWorkoutByID()")
//      workoutViewModel.getWorkoutByID(ObjectId(workoutID))
//    } else if (!workoutID.isNullOrBlank() && copyWorkoutFromPreviousWorkout) {
//      workoutViewModel.copyWorkoutFromPreviousWorkout(ObjectId(workoutID))
//    }
//  }

  /*else {
    workoutViewModel.initWorkoutData()
  }*/

  val previewItems by remember { derivedStateOf { workoutViewModel.previewItems.toList() } } // Observe list changes

  if (previewItems.isNotEmpty()) {
    navController.navigate("previewWorkoutScreen")
  } else {
    WorkoutContent(
      navController = navController,
      workoutViewModel = workoutViewModel,
      currentDestination = currentDestination,
      onNavigateBottomBar = onNavigateBottomBar,
    )
  }

}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutContent(
  navController: NavController,
  workoutViewModel: WorkoutViewModel,
  currentDestination: NavDestination? = null,
  onNavigateBottomBar: (route: String) -> Unit,
) {
  val workout by workoutViewModel.workout.collectAsState()

  //  var openFinishWorkoutDialog by remember { mutableStateOf(false) }

  val (editable, setEditable) = remember { mutableStateOf(true) }

  fun navigateToWorkoutSummaryScreen() {
    //    val workoutSerialized = Json.encodeToString(workoutViewModel.completedWorkout)
    val thisWorkoutID = workoutViewModel.workout.value?.id?.toHexString()
    val workoutsCount = workoutViewModel.getWorkoutsCount()

    Log.i("navigateToWorkoutSummaryScreen()", "workoutCount: $workoutsCount")
    navController.navigate("workoutSummary/$thisWorkoutID/$workoutsCount") {
      val route = navController.currentBackStackEntry?.destination?.route
      route?.let {
        popUpTo(it)
        {
          inclusive = true
        }
      }
    }
  }

  //  if (openFinishWorkoutDialog) {
  //    FinishWorkoutButton(navigateToWorkoutSummaryScreen = { navigateToWorkoutSummaryScreen() })
  //    openFinishWorkoutDialog = false
  //  }

  val openFinishWorkoutDialog = remember { mutableStateOf(false) }
  val openPartialFinishWorkoutDialog = remember { mutableStateOf(false) }
  val openCancelWorkoutDialog = remember { mutableStateOf(false) }

  fun closePartialFinishWorkoutDialog() {
    openPartialFinishWorkoutDialog.value = false
  }

  fun closeFinishWorkoutDialog() {
    openFinishWorkoutDialog.value = false
  }

  fun closeDiscardWorkoutDialog() {
    openCancelWorkoutDialog.value = false
  }

  fun openFinishWorkoutDialog() {
    val (countSetsCompleted, countSets) = workoutViewModel.checkAllSetsComplete()
    Log.i("openFinishWorkoutDialog()", "countSetsCompleted: $countSetsCompleted")
    Log.i("openFinishWorkoutDialog()", "countSets: $countSets")
    when (countSetsCompleted) {
      0 -> openCancelWorkoutDialog.value = true
      countSets -> openFinishWorkoutDialog.value = true
      in 1..<countSets -> openPartialFinishWorkoutDialog.value = true
      else -> openCancelWorkoutDialog.value = true
    }
  }

  if (openFinishWorkoutDialog.value) {
    FinishWorkoutDialog(
      navigateToWorkoutSummaryScreen = { navigateToWorkoutSummaryScreen() },
      openDialog = { closeFinishWorkoutDialog() },
      confirmFinishWorkout = { workoutViewModel.finishWorkout() },
    )
  }

  if (openPartialFinishWorkoutDialog.value) {
    PartialFinishWorkoutDialog(
      navigateToWorkoutSummaryScreen = { navigateToWorkoutSummaryScreen() },
      openDialog = { closePartialFinishWorkoutDialog() },
      confirmFinishWorkout = { workoutViewModel.finishWorkout() },
    )
  }

  if (openCancelWorkoutDialog.value) {
    DiscardWorkoutDialog(
      closeDialog = { closeDiscardWorkoutDialog() },
      cancelWorkout = {
        closeDiscardWorkoutDialog()
        workoutViewModel.cancelWorkout()
        navController.navigateUp()
      },
    )
  }

  var openCalculateBarbellPlatesBottomSheet by rememberSaveable { mutableStateOf(false) }
  val scope = rememberCoroutineScope()
  val calculateBarbellPlatesBottomSheetState =
    rememberModalBottomSheetState(skipPartiallyExpanded = false)

  val exercisesSets by workoutViewModel.workoutComponents.collectAsState(emptyList())
  val workoutDurationString by workoutViewModel.workoutDurationString.collectAsState()
  val isWorkoutCountUpTimerPaused by workoutViewModel.isWorkoutCountUpTimerPaused.collectAsState()


//  workout?.let{
//    File(LocalContext.current.filesDir.absolutePath + "/workout.json").writeText(
//      Json.encodeToString(Workout.serializer(), it)
//    )
//  }
//
//
//  File(LocalContext.current.filesDir.absolutePath + "/workoutComponents.json").writeText(
//    Json.encodeToString(exercisesSets)
//  )

  BackHandler(enabled = true) {
    if (exercisesSets.isEmpty() && isWorkoutCountUpTimerPaused) {
      navController.navigateUp()
    } else {
      openFinishWorkoutDialog()
    }
  }

  //  PredictiveBackHandler(enabled = exercisesSets.isEmpty()) { progress: Flow<BackEventCompat> ->
  //    callback.preparePop()
  //    try {
  //      progress.collect { backEvent -> callback.updateProgress(backEvent.progress) }
  //      callback.popBackStack()
  //    } catch (e: CancellationException) {
  //      callback.cancelPop()
  //    }
  //  }

  Scaffold(
//    modifier = Modifier.safeDrawingPadding(),
    topBar = {
      TopAppBar(
        title = {
          WorkoutNameTextField(
            workout?.name ?: "Workout",
            setWorkoutName = { name -> workoutViewModel.setWorkoutName(name) },
          )
        },
        navigationIcon = {
          /*IconButton(onClick = { navController.navigateUp() }) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen",
            )
          }*/
        },
        actions = {
          RestTimerButton(workoutViewModel = workoutViewModel)
          IconButton(
            onClick = {
              openCalculateBarbellPlatesBottomSheet = !openCalculateBarbellPlatesBottomSheet
            },
            enabled = true
          ) {
            Icon(
              Icons.Outlined.Calculate,
              contentDescription = "Show Calculate Barbell Plates Bottom Sheet"
            )
          }
          WorkoutOptionsDropdownMenu(
            addWorkoutNote = { workoutViewModel.addWorkoutNote() },
            enableWorkoutNameTextfield = { setEditable(!editable) },
            workoutNameDialog = { dismissWorkoutNameDialog ->
              WorkoutNameDialog(
                workoutName = workout?.name ?: "",
                dismissWorkoutNameDialog = { dismissWorkoutNameDialog() },
                setWorkoutName = { workoutViewModel.setWorkoutName(it) },
              )
            },
          )
        },
      )
    },
    bottomBar = {
      BottomNavigationBar(
        currentDestination = currentDestination,
        onNavigate = onNavigateBottomBar,
      )
    },
  ) { contentPadding ->
    Column(Modifier.padding(contentPadding)) {
      Row(
        modifier = Modifier
          .padding(start = 8.dp)
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        WorkoutDuration(workoutViewModel = workoutViewModel)
        FinishWorkoutButton(
          workoutViewModel = workoutViewModel,
          navigateToWorkoutSummaryScreen = { navigateToWorkoutSummaryScreen() })
      }

      ExercisesSetsScreen(navController = navController, workoutViewModel = workoutViewModel)

      if (openCalculateBarbellPlatesBottomSheet) {
        CalculateBarbellPlatesBottomSheet(
          bottomSheetState = calculateBarbellPlatesBottomSheetState,
          expandBottomSheet = { scope.launch { calculateBarbellPlatesBottomSheetState.expand() } },
          onDismissRequest = {
            openCalculateBarbellPlatesBottomSheet = !openCalculateBarbellPlatesBottomSheet
          },
          calculatePlates = { target, bar, plates ->
            val perSide = (target - bar) / 2
            val result = mutableMapOf<Float, Int>()
            var remaining = perSide
            for (p in plates) {
              val c = (remaining / p).toInt()
              if (c > 0) {
                result[p] = c
                remaining -= c * p
              }
            }
            result
          },
        )
      }

    }
  }
}

@Composable
private fun WorkoutDuration(workoutViewModel: WorkoutViewModel) {
  val workoutDurationString by workoutViewModel.workoutDurationString.collectAsState()
  val isWorkoutCountUpTimerPaused by workoutViewModel.isWorkoutCountUpTimerPaused.collectAsState()

  Button(
    onClick = {
      if (isWorkoutCountUpTimerPaused) workoutViewModel.startWorkoutTimer()
      else workoutViewModel.pauseWorkoutTimer()
    },
    modifier = Modifier.padding(8.dp),
  ) {
    val colorAlpha by
    animateFloatAsState(
      targetValue =
        if (isWorkoutCountUpTimerPaused && workoutDurationString != "0:00") 0f else 1f,
      animationSpec = infiniteRepeatable(animation = tween(1000)),
      label = "textColorAlpha",
    )
    Text(
      text =
        if (isWorkoutCountUpTimerPaused && workoutDurationString == "0:00") "Start Workout Clock"
        else workoutDurationString,
      style = MaterialTheme.typography.bodyLarge,
      color =
        MaterialTheme.colorScheme.onPrimary.copy(
          alpha =
            if (isWorkoutCountUpTimerPaused && workoutDurationString != "0:00") colorAlpha else 1f
        ),
    )
  }
}

@Composable
private fun WorkoutNameTextField(workoutName: String, setWorkoutName: (String) -> Unit) {
  var name by remember { mutableStateOf(workoutName) }

  LaunchedEffect(workoutName) {
    name = workoutName
  }

  BasicTextField(
    enabled = false,
    modifier = Modifier.padding(PaddingValues(4.dp)),
    textStyle = MaterialTheme.typography.titleLarge.copy(
      fontSize = MaterialTheme.typography.titleMedium.fontSize.times(1.1),
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground,
    ),
    value = name,
    onValueChange = {
      name = it
      setWorkoutName(it)
    },
  )
}

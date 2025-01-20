package com.example.myapplication.components.workout

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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.components.BottomNavigationBar
import com.example.myapplication.components.DiscardWorkoutDialog
import com.example.myapplication.components.WorkoutNameDialog
import com.example.myapplication.components.WorkoutOptionsDropdownMenu
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.components.setRestTimer.RestTimerButton
import org.bson.types.ObjectId
import org.koin.androidx.compose.koinViewModel

// TODO Takes a Workout Object

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun WorkoutScreen(
  navController: NavController,
  workoutViewModel: WorkoutViewModel = koinViewModel(),
  workoutID: String?,
) {
  if (!workoutID.isNullOrBlank()) {
    workoutViewModel.getWorkoutByID(ObjectId(workoutID))
  } else {
    workoutViewModel.initWorkoutData()
  }

  WorkoutContent(navController = navController)
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutContent(
  navController: NavController,
  workoutViewModel: WorkoutViewModel = koinViewModel(),
) {
  val workoutName by workoutViewModel.workoutName.collectAsState()

  //  var openFinishWorkoutDialog by remember { mutableStateOf(false) }

  val (editable, setEditable) = remember { mutableStateOf(true) }

  fun navigateToWorkoutSummaryScreen() {
    //    val workoutSerialized = Json.encodeToString(workoutViewModel.completedWorkout)
    val thisWorkoutID = workoutViewModel.workout.value._id.toHexString()
    val workoutsCount = workoutViewModel.getWorkoutsCount()

    Log.i("navigateToWorkoutSummaryScreen()", "workoutCount: $workoutsCount")
    navController.navigate("workoutSummary/$thisWorkoutID/$workoutsCount")
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
    openFinishWorkoutDialog.value = false
  }

  fun openFinishWorkoutDialog() {
    val (countSetsCompleted, countSets) = workoutViewModel.checkAllSetsComplete()
    when (countSetsCompleted) {
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
      openDialog = { closeDiscardWorkoutDialog() },
      cancelWorkout = {
          closeDiscardWorkoutDialog()
        workoutViewModel.cancelWorkout()
          navController.navigateUp()
      },
    )
  }

  val exercisesSets by workoutViewModel.exercisesSets.collectAsState(emptyList())
  val workoutDurationString by workoutViewModel.workoutDurationString.collectAsState()
  val isWorkoutCountUpTimerPaused by workoutViewModel.isWorkoutCountUpTimerPaused.collectAsState()

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
    topBar = {
      TopAppBar(
        title = {
          WorkoutNameTextField(
            workoutName ?: "Workout",
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
          RestTimerButton()
          WorkoutOptionsDropdownMenu(
            addWorkoutNote = { workoutViewModel.addWorkoutNote() },
            enableWorkoutNameTextfield = { setEditable(!editable) },
            workoutNameDialog = { dismissWorkoutNameDialog ->
              WorkoutNameDialog(
                workoutName = workoutName ?: "",
                dismissWorkoutNameDialog = { dismissWorkoutNameDialog() },
                setWorkoutName = { workoutViewModel.setWorkoutName(it) },
              )
            },
          )
        },
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
        verticalAlignment = Alignment.CenterVertically,
      ) {
        WorkoutDuration()
        FinishWorkoutButton(navigateToWorkoutSummaryScreen = { navigateToWorkoutSummaryScreen() })
      }

      ExercisesSetsScreen(navController = navController)
    }
  }
}

@Composable
private fun WorkoutDuration(workoutViewModel: WorkoutViewModel = koinViewModel()) {
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

  BasicTextField(
    enabled = false,
    modifier = Modifier.padding(PaddingValues(4.dp)),
    textStyle =
      TextStyle(
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
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

package com.example.myapplication.features.history.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.features.history.HistoryViewModel
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun WorkoutOverview(
  historyViewModel: HistoryViewModel,
  workoutID: String,
  //  getExercise: (exerciseID: ObjectId) -> Exercise?,
  navigateUp: () -> Unit,
  popBackStack: () -> Unit,
  workoutOverviewContent: @Composable (workout: Workout, setOpenDeleteWorkoutDialog: () -> Unit) -> Unit
) {

  BackHandler(enabled = true) {
    navigateUp()
  }

  val toastMessage = historyViewModel.toastMessage.collectAsStateWithLifecycle("")

  if (toastMessage.value.isNotEmpty()) {
    Snackbar {
      Text(text = toastMessage.value)
    }
  }

  historyViewModel.getWorkoutByID(ObjectId(workoutID))
  val workout by historyViewModel.workout.collectAsState()

  workout?.workoutItems?.let { historyViewModel.loadWorkoutItemsFromWorkout(it) }
//  val workoutComponents by historyViewModel.workoutComponents.collectAsState()

  val (openDeleteWorkoutDialog, setOpenDeleteWorkoutDialog) = remember { mutableStateOf(false) }

  if (openDeleteWorkoutDialog) {
    workout?.let {
      ConfirmDeleteWorkout(
        onDismissRequest = { setOpenDeleteWorkoutDialog(false) },
        deleteWorkout = {
          historyViewModel.deleteWorkout(it)
          popBackStack()
        },
      )
    }
  }

  when (workout) {
    null -> NoWorkoutFound()
    else ->
      workout?.let {
        workoutOverviewContent(it, { setOpenDeleteWorkoutDialog(true) })
      }
  }
}

@Composable
fun NoWorkoutFound() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(imageVector = Icons.Filled.Error, contentDescription = null)
    Text(text = "No Workout Found!")
  }
}

@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.previewNextWorkout

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.myapplication.features.workout.WorkoutViewModel
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

// TODO Call conditionally

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PreviewWorkoutScreen(
  navController: NavController,
  workoutViewModel: WorkoutViewModel,
  workoutID: String,
  WorkoutOverview: @Composable () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()

  BackHandler(scaffoldNavigator.canNavigateBack()) {
    coroutineScope.launch { scaffoldNavigator.navigateBack() }
  }

  val pagerState = rememberPagerState(pageCount = { 2 })

  Scaffold { paddingValues ->
    HorizontalPager(state = pagerState, modifier = Modifier.padding(paddingValues)) {
      when (it) {
        0 -> {
          Column {
            val workout by workoutViewModel.workout.collectAsState(null)
            val workoutComponents by workoutViewModel.workoutComponents.collectAsState()
            val workoutNotes by workoutViewModel.workoutNotes.collectAsState()
            val workoutName by workoutViewModel.workoutName.collectAsState()

            EditNextWorkoutScreenStateHolder(
              workoutViewModel = workoutViewModel,
              workoutID = workoutID,
              navController = navController,
              editable = true,
            )
          }
        }

        1 -> {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Start,
            ) {
              Text("Previous Workout")
            }

            WorkoutOverview()
          }
        }
      }
    }
  }
}

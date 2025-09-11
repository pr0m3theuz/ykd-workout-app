@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.example.myapplication.features.previewNextWorkout

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.defaultDragHandleSemantics
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutComponentListSerializer
import com.example.myapplication.features.history.ui.WorkoutOverviewContent
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

// TODO try a different layout e.g. list-detail layout

@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewNextWorkoutScreenPreview() {
  val json = Json {
    ignoreUnknownKeys = true // Recommended for flexibility
    isLenient = true // If JSON might be slightly malformed
  }

  val nextWorkoutFile =
    LocalContext.current.assets.open("next_workout.json").bufferedReader().readLines()

  val nextWorkoutComponentsFile =
    LocalContext.current.assets.open("next_workoutComponents.json").bufferedReader().readText()

  val previousWorkoutFile =
    LocalContext.current.assets.open("previous_workout.json").bufferedReader().readLines()

  val previousWorkoutComponentsFile =
    LocalContext.current.assets.open("previous_workoutComponents.json").bufferedReader().readText()

  val previousWorkout =
    previousWorkoutFile
      .mapNotNull map@{
        var workout: Workout? = null
        try {
          workout = json.decodeFromString<Workout>(it)
        } catch (e: Exception) {
          println("Error processing document ID : ${e.message}")
          println(it)
          e.printStackTrace()
        }
        return@map workout
      }
      .firstOrNull()

  val nextWorkout =
    nextWorkoutFile
      .mapNotNull map@{
        var workout: Workout? = null
        try {
          workout = json.decodeFromString<Workout>(it)
        } catch (e: Exception) {
          println("Error processing document ID : ${e.message}")
          println(it)
          e.printStackTrace()
        }
        return@map workout
      }
      .firstOrNull()

  val nextWorkoutComponents =
    json.decodeFromString(WorkoutComponentListSerializer, nextWorkoutComponentsFile)

  val previousWorkoutComponents =
    json.decodeFromString(WorkoutComponentListSerializer, previousWorkoutComponentsFile)

  val nextWorkoutUiState =
    EditNextWorkoutUiState(
      workout = nextWorkout,
      workoutComponents = nextWorkoutComponents,
      workoutNotes = nextWorkout?.notes ?: emptyList(),
      workoutName = nextWorkout?.name ?: "No Name",
      isLoading = false,
      errorMessage = null,
      userDefaultWeightUnit = MassUnit.POUNDS.abbreviation,
    )

  val previousWorkoutUiState =
    EditNextWorkoutUiState(
      workout = previousWorkout,
      workoutComponents = previousWorkoutComponents,
      workoutNotes = previousWorkout?.notes ?: emptyList(),
      workoutName = previousWorkout?.name ?: "No Name",
      isLoading = false,
      errorMessage = null,
      userDefaultWeightUnit = MassUnit.POUNDS.abbreviation,
    )

  val coroutineScope = rememberCoroutineScope()
  val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()

  BackHandler(scaffoldNavigator.canNavigateBack()) {
    coroutineScope.launch { scaffoldNavigator.navigateBack() }
  }

  Surface {
    SupportingPaneScaffold(
      directive = scaffoldNavigator.scaffoldDirective,
      value = scaffoldNavigator.scaffoldValue,
      paneExpansionState = rememberPaneExpansionState(scaffoldNavigator.scaffoldValue),
      paneExpansionDragHandle = { state ->
        val interactionSource = remember { MutableInteractionSource() }

        VerticalDragHandle(
          modifier =
            Modifier.paneExpansionDraggable(
              state,
              minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
              interactionSource = interactionSource,
              semanticsProperties = state.defaultDragHandleSemantics(),
            ),
          interactionSource = interactionSource,
        )
      },
      mainPane = {
        AnimatedPane(modifier = Modifier.systemBarsPadding()) {
          Column {
            Row(
              modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Start,
            ) {
              if (
                scaffoldNavigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] ==
                PaneAdaptedValue.Hidden
              ) {
                Button(
                  modifier = Modifier.wrapContentSize(),
                  onClick = {
                    coroutineScope.launch {
                      scaffoldNavigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                    }
                  },
                ) {
                  Text("Show Previous Workout")
                }
              } /*else {
                  Text("Supporting pane is shown")
                }*/
            }
            // TODO Design Better Screen. Replace Set Content with CommonComponents.SetContent & use
            // target values
            EditNextWorkoutScreenContent(
              uiState = nextWorkoutUiState,
              onEvent = {}, // Pass the event handler
              navController = rememberNavController(),
              editable = false,
              populatePreviousSetField = { _, _, _ -> null },
              ExerciseOptionsDropdownMenu = { exerciseSets, addNotes -> },
            )
          }
        }
      },
      supportingPane = {
        AnimatedPane(modifier = Modifier.systemBarsPadding()) {
          // Supporting pane content
          Column {
            Row(
              modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Start,
            ) {
              Text("Previous Workout")
            }
            previousWorkoutUiState.workout?.let {
              WorkoutOverviewContent(
                workout = it,
                setOpenDeleteWorkoutDialog = {},
                performAgain = false,
                userDefaultWeightUnit = previousWorkoutUiState.userDefaultWeightUnit,
                workoutComponents = previousWorkoutUiState.workoutComponents,
                navigateUp = {},
                navigateToEditWorkout = { workout -> },
                navigateToWorkout = { workout -> },
              )
            }
          }
        }
      },
    )
  }
}

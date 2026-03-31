package com.example.myapplication.features.program.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.LoadingWorkout
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.program.ProgramsViewModel
import kotlin.uuid.ExperimentalUuidApi

// @Preview
@OptIn(ExperimentalUuidApi::class)
@Composable
fun ActiveWorkoutCard(
  workout: Workout,
  programsViewModel: ProgramsViewModel,
  navigateToActiveProgramDetails: (String) -> Unit = {},
  navigateToPausedWorkout: (String, Int) -> Unit = { _, _ -> },
) {
  // TODO Get the next workout in the program
  // TODO Add programID to Workout
  // TODO Write query to get next workout in program
  val padding = 8.dp

  val context = LocalContext.current

  val titleSmall = MaterialTheme.typography.titleSmall
  val (expandMenu, setExpandMenu) = remember { mutableStateOf(false) }

  val bodySmall = MaterialTheme.typography.bodySmall

  Card(modifier = Modifier.padding(vertical = padding)) {


    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = padding),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = workout.name,
        modifier = Modifier.padding(horizontal = 8.dp),
        style = MaterialTheme.typography.titleMedium,
      )
      Box {
        IconButton(onClick = { setExpandMenu(true) }) {
          Icon(
            imageVector = Icons.Outlined.MoreVert,
            contentDescription = "Localized Description"
          )
        }

        DropdownMenu(expanded = expandMenu, onDismissRequest = { setExpandMenu(false) }) {
          // DropdownMenuItem(text = { /*TODO*/}, onClick = { /*TODO*/})
          DropdownMenuItem(
            text = { Text(text = "Delete Workout") },
            onClick = {
              programsViewModel.deleteCurrentlyActiveWorkout(workoutID = workout.id)

              val result =
                programsViewModel.deleteCurrentlyActiveWorkout(workoutID = workout.id)
              if (result) {
                Toast.makeText(context, "Workout deleted successfully.", Toast.LENGTH_SHORT)
                  .show()
              } else {
                Toast.makeText(
                  context,
                  "Workout was not deleted successfully.",
                  Toast.LENGTH_SHORT
                ).show()
              }
            },
          )
        }
      }
    }

    workout.programID?.let { programID ->
      val program = programsViewModel.getCurrentlyActiveProgramByID(programID)

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = padding),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        program?.let {
          Log.d("Programs", "program: $program")
          Text(
            text = program.name ?: "Program Name",
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            modifier =
              Modifier
                .clickable {
                  workout.programID?.toHexString()?.let { p1 -> navigateToActiveProgramDetails(p1) }
                }
          )
        }
      }
    }

    if (workout.workoutItems.isEmpty()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = padding)
      ) {
        Text("No exercises have been added to this workout.", style = bodySmall)
      }
    } else {
    Row(
      modifier =
        Modifier
          .fillMaxWidth()
          .padding(horizontal = padding)
          .padding(top = 12.dp, bottom = 2.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(text = "Exercise", style = titleSmall, modifier = Modifier.weight(0.5f))
      Box(Modifier.weight(0.5f)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
          Text(
            text = "Sets",
            style = titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(weight = 1f, fill = true),
          )
          Text(
            text = "Top Set",
            style = titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(weight = 1f, fill = true),
          )
          Text(
            text = "RIR",
            style = titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(weight = 1f, fill = true),
          )
        }
      }
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = padding)
    ) {
      Column() {
        // TODO List Exercises
        //        Text("Workout Summary", style = bodySmall)

          workout.workoutItems.forEach { workoutItem ->
            when (workoutItem) {
              is ExerciseSets -> {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  // Log.i("getExerciseNameByID", "exercise ID: " +
                  // workoutItem.exerciseID?.toHexString())
                  Text(
                    text = programsViewModel.getExerciseNameByID(workoutItem.exerciseID)
                      ?: "Exercise not found.",
                    style = bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(0.5f),
                  )

                  Box(Modifier.weight(0.5f)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                      Text(
                        text = workoutItem.sets.size.toString(),
                        style = bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(weight = 1f, fill = true),
                      )
                      Text(
                        text =
                          if (
                            workoutItem.sets
                              .maxBy { it.repetitions ?: 0 }
                              .targetRepetitions != null
                          )
                            "${workoutItem.sets.maxBy { it.repetitions ?: 0 }.targetRepetitions} reps"
                          else "-",
                        style = bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(weight = 1f, fill = true),
                      )
                      Text(
                        text =
                          doubleFormatted(
                            workoutItem.sets
                              .minBy { it.repetitionsInReserve ?: 10.0 }
                              .targetRepetitionsInReserve
                          ),
                        style = bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(weight = 1f, fill = true),
                      )
                    }
                  }
                }
              }

              is Superset -> {
                workoutItem.exercisesSets.forEach { exerciseSets ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Text(
                      text = programsViewModel.getExerciseNameByID(exerciseSets.exerciseID)
                        ?: "Exercise not found.",
                      style = bodySmall,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      modifier = Modifier.weight(0.5f),
                    )

                    Box(Modifier.weight(0.5f)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                      ) {
                        Text(
                          text = exerciseSets.sets.size.toString(),
                          style = bodySmall,
                          textAlign = TextAlign.Center,
                          modifier = Modifier.weight(weight = 1f, fill = true),
                        )
                        Text(
                          text =
                            if (
                              exerciseSets.sets
                                .maxBy { it.repetitions ?: 0 }
                                .targetRepetitions
                              != null
                            )
                              "${exerciseSets.sets.maxBy { it.repetitions ?: 0 }.targetRepetitions} reps"
                            else "-",
                          style = bodySmall,
                          textAlign = TextAlign.Center,
                          modifier = Modifier.weight(weight = 1f, fill = true),
                        )
                        Text(
                          text =
                            doubleFormatted(
                              exerciseSets.sets
                                .minBy { it.repetitionsInReserve ?: 10.0 }
                                .targetRepetitionsInReserve
                            ),
                          style = bodySmall,
                          textAlign = TextAlign.Center,
                          modifier = Modifier.weight(weight = 1f, fill = true),
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

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(padding),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Button(
        onClick = {
          navigateToPausedWorkout(
            workout.id.toHexString(),
            LoadingWorkout.LoadWorkoutFromActiveProgram.value
          )
        }
      ) {
//          Text(text = "Resume ${workout.name}")
        Text(text = "Resume Workout")
      }

      if (workout.programID != null) {
        IconButton(
          onClick = {
            workout.programID?.toHexString()?.let { p1 -> navigateToActiveProgramDetails(p1) }
          }
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Outlined.LibraryBooks,
            contentDescription = "Navigate to Program for Workout",
          )
        }
      }
    }
  }
}
package com.example.myapplication.features.program.ui

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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.program.ProgramsViewModel
import kotlin.uuid.ExperimentalUuidApi

// @Preview
@OptIn(ExperimentalUuidApi::class)
@Composable
fun CurrentProgramCard(
  navController: NavController,
  program: Program,
  workout: Workout,
  programsViewModel: ProgramsViewModel,
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
        text = program.name ?: "Program Name",
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleMedium,
        modifier =
        Modifier
          .clickable {
            /* TODO Navigate to Program Details Screen */
            navController.navigate("programDetails/${program.id.toHexString()}")
          }
          .weight(1f, fill = true),
      )
      Box {
        IconButton(onClick = { setExpandMenu(true) }) {
          Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "Localized Description")
        }

        DropdownMenu(expanded = expandMenu, onDismissRequest = { setExpandMenu(false) }) {
          // DropdownMenuItem(text = { /*TODO*/}, onClick = { /*TODO*/})
          DropdownMenuItem(
            text = { Text(text = "Restart Program") },
            onClick = { /*TODO*/
              Toast.makeText(context, "TODO Restart Program", Toast.LENGTH_SHORT).show()
            },
          )
          DropdownMenuItem(
            text = { Text(text = "Archive Program") },
            onClick = { /*TODO*/
              Toast.makeText(context, "TODO Archive Program", Toast.LENGTH_SHORT).show()
            },
          )
          DropdownMenuItem(
            text = { Text(text = "Delete Program") },
            onClick = { /*TODO*/
              val result = programsViewModel.deleteActiveProgram(program)
              if (result.isSuccess) {
                result.onSuccess { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
              } else {
                result.onFailure { Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show() }
              }
            },
          )
        }
      }
    }

    Text(
      text = workout.name,
      modifier = Modifier.padding(horizontal = 8.dp),
      style = bodySmall,
    )
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
                          .targetRepetitions
                        != null
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

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(padding),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Button(
        onClick = {
          // TODO
          navController.navigate("workoutScreen?workoutID=${workout.id.toHexString()}/copyWorkoutFromPreviousWorkout=false") {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
          }
        }
      ) {
        Text(text = "Start ${workout.name}")
      }

      IconButton(
        onClick = { /*TODO*/
          // TODO Navigate to Program's Workout Tab
          navController.navigate("programDetails/${program.id.toHexString()}")
        }
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Outlined.LibraryBooks,
          contentDescription = "Localized Description",
        )
      }
    }
  }
}
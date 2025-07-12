package com.example.myapplication.features.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.SupersetExercisesSets
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutComponent
import com.example.myapplication.common.domain.model.formatLongWorkoutStarted
import com.example.myapplication.common.domain.model.formatWorkoutDuration
import com.example.myapplication.common.domain.model.setSummaryRIR
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.workout.ui.EditWorkoutOptionsDropdownMenu

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun WorkoutOverviewContent(
  workout: Workout,
  setOpenDeleteWorkoutDialog: (Boolean) -> Unit,
  performAgain: Boolean,
  userDefaultWeightUnit: String,
  workoutComponents: List<WorkoutComponent>,
  navigateUp: () -> Unit,
  navigateToEditWorkout: (Workout) -> Unit,
  navigateToWorkout: (Workout) -> Unit,
) {
  Scaffold(
    modifier = Modifier.imePadding(),
    topBar = {
      TopAppBar(
        title = {
          Text(text = workout.name, style = MaterialTheme.typography.titleLarge)
        },
        navigationIcon = {
          IconButton(onClick = { navigateUp() }) {
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
              navigateToEditWorkout(workout)
//              navController.navigate("history?editWorkout=${workout.id.toHexString()}")
            },
            deleteWorkout = { setOpenDeleteWorkoutDialog(true) },
            saveWorkoutAsTemplate = { /* TODO Create Save Workout As Template Function */ },
            //              workoutNameDialog = {}
          )
        },
      )
    },
    bottomBar = {
      if (performAgain) {
        Button(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          onClick = {
            navigateToWorkout(workout)


          },
        ) {
          Text(text = "Perform Again")
        }
      }
      //        BottomNavigationBar(navController = navController)
    },
  ) { contentPadding ->
    // TODO Change to LazyColumn

    Column(
      Modifier
        .padding(contentPadding)
        .verticalScroll(rememberScrollState())
        .fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Column(Modifier.padding(horizontal = 12.dp)) {
        Row(
          modifier = Modifier
            .padding()
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
            Text(workout.formatLongWorkoutStarted())
          }
        }
        Row(
          modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
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
              Text(text = workout.formatWorkoutDuration())
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
                      "kg" -> workout.totalVolumeInKilograms
                      else -> workout.totalVolumeInPounds
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
            workout.notes.forEach { workoutNote ->
              Row { Text(text = workoutNote.note ?: "") }
            }
          }
        }

        workoutComponents.forEach { workoutComponent ->
          Box(
            modifier = Modifier
              .padding(vertical = 16.dp)
              .fillMaxWidth()
          ) {
            Column(modifier = Modifier.fillMaxWidth()) {
              when (workoutComponent) {
                is ExerciseExerciseSets -> {
                  Row {
                    Text(
                      modifier = Modifier.padding(vertical = 8.dp),
                      text = workoutComponent.exercise.name,
                      style = MaterialTheme.typography.titleMedium,
                    )
                  }
                  workoutComponent.sets.forEach { set ->
                    // TODO Chanage Text Color based on Set Type
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
                      Text(
                        text =
                        "${
                          if (set.setTypeID == SetType.normalSet) {
                            set.setNumber
                          } else {
                            set.setTypeID.abbreviation
                          }
                        }.   ${set.setSummaryRIR(userDefaultWeightUnit)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace
                      )
                    }
                  }
                }

                is SupersetExercisesSets -> {
                  workoutComponent.exercisesSets.forEach { exerciseSets ->
                    Row {
                      Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = exerciseSets.exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                      )
                    }
                    exerciseSets.sets.forEach { set ->
                      // TODO Chanage Text Color based on Set Type
                      Row {
                        Text(
                          text =
                          "${
                            if (set.setTypeID == SetType.normalSet) {
                              set.setNumber?.plus(1).toString()
                            } else {
                              set.setTypeID.abbreviation
                            }
                          }   ${set.setSummaryRIR(userDefaultWeightUnit)}",
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
}
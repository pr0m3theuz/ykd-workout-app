package com.example.myapplication.components.workout

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.components.BottomNavigationBar
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.model.formatWorkoutDate
import com.example.myapplication.data.model.formatWorkoutDuration
import com.example.myapplication.data.model.setTextRIR
import com.example.myapplication.data.model.setVolume
import com.example.myapplication.data.model.totalSets
import com.example.myapplication.utils.doubleFormatted
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import org.mongodb.kbson.ObjectId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSummaryScreen(
  navController: NavController,
  workoutViewModel: WorkoutViewModel = koinViewModel(),
  workoutID: String,
  workoutsCount: Int,
) {
  workoutViewModel.getCompletedWorkout(ObjectId.invoke(workoutID))
  val completedWorkout by workoutViewModel.completedWorkout.collectAsStateWithLifecycle()
  val userDefaultWeightUnit = workoutViewModel.userDefaultWeightUnit

  Log.i("WorkoutSummaryScreen", "workoutID: $workoutID")
  Log.i("WorkoutSummaryScreen", "completedWorkout: ${Json.encodeToString(completedWorkout)}")

  //  Log.i("WorkoutSummaryScreen", "workoutViewModel: $workoutViewModel")

  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(
            onClick = {
              /*TODO Navigate to Previous Screen */
              // TODO This is an anti-pattern
              // https://developer.android.com/guide/navigation/use-graph/navigate
              navController.navigate("programs") { popUpTo("programs") { inclusive = false } }
            }
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen",
            )
          }
        },
        actions = {},
      )
    },
    // TODO This is an anti-pattern
    // https://developer.android.com/guide/navigation/use-graph/navigate
    bottomBar = { BottomNavigationBar(navController = navController) },
  ) { contentPadding ->
    Column(Modifier.padding(contentPadding)) {
      if (completedWorkout != null) {
        WorkoutSummaryContent(
          completedWorkout = completedWorkout!!,
          workoutsCount,
          userDefaultWeightUnit,
          workoutViewModel,
        )
      } /*else {
        navController.navigate("programs")*/
      /* { popUpTo("programs") { inclusive = false } }*/
      /*
      }*/
    }
  }
}

// @Preview(showSystemUi = true, showBackground = true)
@Composable
fun WorkoutSummaryContent(
  completedWorkout: Workout,
  workoutsCount: Int,
  userDefaultWeightUnit: String,
  workoutViewModel: WorkoutViewModel,
) {

  Surface {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalArrangement = Arrangement.Center,
      ) {
        Text(text = "You crushed it!", style = MaterialTheme.typography.titleLarge)
      }

      Row(
        modifier = Modifier.fillMaxWidth().padding(0.dp),
        horizontalArrangement = Arrangement.Center,
      ) {
        //        TODO Query Workout Count
        Text(text = "You completed your ${workoutsCount + 1} workout!")
      }
      Spacer(modifier = Modifier.height(24.dp))
      WorkoutSummaryCard(completedWorkout, userDefaultWeightUnit) { exerciseID ->
        workoutViewModel.getExerciseByID(exerciseID)
      }
    }
  }
}

@Composable
fun WorkoutSummaryCard(
  completedWorkout: Workout,
  userDefaultWeightUnit: String,
  getExercise: (exerciseID: ObjectId?) -> Exercise?,
) {
  val totalSets = completedWorkout.totalSets()
  val totalVolume =
    doubleFormatted(
      when (userDefaultWeightUnit) {
        "kg" -> completedWorkout.totalVolumeInKilograms
        else -> completedWorkout.totalVolumeInPounds
      }
    )

  Card(modifier = Modifier.padding(vertical = 0.dp)) {
    Column(modifier = Modifier.padding(8.dp)) {
      Row { Text(text = "${completedWorkout.name}", style = MaterialTheme.typography.titleMedium) }

      Row {
        Text(
          text = completedWorkout.formatWorkoutDate(),
          style = MaterialTheme.typography.bodyMedium,
        )
      }

      Row(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
      ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
            ) {
              Icon(Icons.Filled.Timer, contentDescription = null)
              Text(text = "Duration")
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
            ) {
              Text(text = completedWorkout.formatWorkoutDuration())
            }
          }
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
            ) {
              val weightIcon = painterResource(R.drawable.weight_fill1_wght400_grad0_opsz24)
              //                  Image(painter = weightIcon, contentDescription = null)
              Icon(painter = weightIcon, contentDescription = null)
              Text(text = "Volume")
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
            ) {
              // TODO Get Default Weight Unit from User Settings
              Text(text = "$totalVolume $userDefaultWeightUnit")
            }
          }
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
            ) {
              Icon(Icons.Filled.FormatListNumbered, contentDescription = null)
              Text(text = "Sets")
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
            ) {
              Text(text = "$totalSets")
            }
          }
        }
      }

      val backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .border(4.dp, backgroundColor)
            .padding(4.dp)
            .background(color = backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        val style = MaterialTheme.typography.titleMedium
        Text(text = "Exercise", style = style, modifier = Modifier.weight(1f))
        Text(text = "Top Set", style = style, modifier = Modifier.weight(0.5f))
      }
      completedWorkout.exercisesSets.forEach { exerciseSets ->
        // TODO Seperate from WorkoutViewModel
        val exercise = getExercise(exerciseSets.exerciseID)
        Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          val weightUnit = exercise?.weightUnit ?: userDefaultWeightUnit
          val style = MaterialTheme.typography.bodyMedium
          // TODO Change to Total Volume or Max Weight or lowest RIR for Top Set
          val maxWeight = exerciseSets.sets.maxBy { it.weightPounds ?: 0.0 }
          val maxRIR = exerciseSets.sets.maxBy { it.repetitionsInReserve ?: 10.0 }
          val maxVolume = exerciseSets.sets.maxBy { it.setVolume(weightUnit) }

          Text(
            text = "${exerciseSets.sets.size} × ${exercise?.name ?: ""}",
            style = style,
            modifier = Modifier.weight(1f),
          )

          Text(
            // RIR
            text = maxWeight.setTextRIR(weightUnit),
            style = style,
            modifier = Modifier.weight(0.5f),
          )
        }
      }
    }
  }
}

/*
class WorkoutDataProvider : PreviewParameterProvider<Workout> {
  @OptIn(ExperimentalTime::class)
  override val values: Sequence<Workout>
    get() =
      sequenceOf(
        Workout().apply {
          name = "Test Workout"
          dateCreated = RealmInstant.now()
          workoutStarted = RealmInstant.now()
          workoutEnded = RealmInstant.now()
          workoutDuration =
            Duration.convert(69.0, DurationUnit.MINUTES, DurationUnit.MILLISECONDS).toLong()
          exercisesSets =
            realmListOf(
              ExerciseSets().apply {
                exerciseID = Exercise().apply { name = "Test Exercise" }
                sets =
                  realmListOf(
                    Set().apply {
                      setNumber = 1
                      weightPounds = 10.0
                      repetitions = 10
                      ratePerceivedEffort = 10.0
                      repetitionsInReserve = 0.0
                      setTypeID = 1
                      percentage1RM = 1.0
                    },
                    Set().apply {
                      setNumber = 1
                      weightPounds = 10.0
                      repetitions = 10
                      ratePerceivedEffort = 10.0
                      repetitionsInReserve = 0.0
                      setTypeID = 1
                      percentage1RM = 1.0
                    },
                    Set().apply {
                      setNumber = 1
                      weightPounds = 10.0
                      repetitions = 10
                      ratePerceivedEffort = 10.0
                      repetitionsInReserve = 0.0
                      setTypeID = 1
                      percentage1RM = 1.0
                    }
                  )
              },
              ExerciseSets().apply {
                exerciseID = Exercise().apply { name = "Test Exercise" }
                sets =
                  realmListOf(
                    Set().apply {
                      setNumber = 1
                      weightPounds = 10.0
                      repetitions = 10
                      ratePerceivedEffort = 10.0
                      repetitionsInReserve = 0.0
                      setTypeID = 1
                      percentage1RM = 1.0
                    },
                    Set().apply {
                      setNumber = 1
                      weightPounds = 10.0
                      repetitions = 10
                      ratePerceivedEffort = 10.0
                      repetitionsInReserve = 0.0
                      setTypeID = 1
                      percentage1RM = 1.0
                    },
                    Set().apply {
                      setNumber = 1
                      weightPounds = 10.0
                      repetitions = 10
                      ratePerceivedEffort = 10.0
                      repetitionsInReserve = 0.0
                      setTypeID = 1
                      percentage1RM = 1.0
                    }
                  )
              },
              ExerciseSets().apply {
                exerciseID = Exercise().apply { name = "Test Exercise" }
                sets =
                  realmListOf(
                    Set().apply {
                      setNumber = 1
                      weightPounds = 10.0
                      repetitions = 10
                      ratePerceivedEffort = 10.0
                      repetitionsInReserve = 0.0
                      setTypeID = 1
                      percentage1RM = 1.0
                    },
                    Set().apply {
                      setNumber = 1
                      weightPounds = 10.0
                      repetitions = 10
                      ratePerceivedEffort = 10.0
                      repetitionsInReserve = 0.0
                      setTypeID = 1
                      percentage1RM = 1.0
                    },
                    Set().apply {
                      setNumber = 1
                      weightPounds = 10.0
                      repetitions = 10
                      ratePerceivedEffort = 10.0
                      repetitionsInReserve = 0.0
                      setTypeID = 1
                      percentage1RM = 1.0
                    }
                  )
              },
            )
        }
      )
}
*/

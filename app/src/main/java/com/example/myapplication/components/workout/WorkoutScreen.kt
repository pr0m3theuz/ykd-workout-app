package com.example.myapplication.components.workout

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.example.myapplication.components.BottomNavigationBar
import com.example.myapplication.components.FinishWorkoutButton
import com.example.myapplication.components.WorkoutOptionsDropdownMenu
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.components.setRestTimer.RestTimerButton
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel

// TODO Takes a Workout Object

/*@Composable
fun WorkoutScreen(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "workoutContent") {

    }
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutContent(
  navController: NavController,
  workoutViewModel: WorkoutViewModel = koinViewModel()
) {
  val workoutName by workoutViewModel.workoutName.collectAsState()

  var openFinishWorkoutDialog by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val (editable, setEditable) = remember { mutableStateOf(true) }

  fun navigateToWorkoutSummaryScreen() {
    val workoutSerialized = Json.encodeToString(workoutViewModel.completedWorkout)
    val workoutsCount = workoutViewModel.getWorkoutsCount()
    navController.navigate("workoutSummary/$workoutSerialized/$workoutsCount") {
      popUpTo("workoutSummary") { inclusive = true }
    }
  }

  if (openFinishWorkoutDialog) {
    FinishWorkoutButton(navigateToWorkoutSummaryScreen = { navigateToWorkoutSummaryScreen() })
    openFinishWorkoutDialog = false
  }

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
          IconButton(
            onClick = {
              /*TODO Navigate to Previous Screen */
              navController.navigateUp()
              Toast.makeText(context, "TODO: Navigate to Previous Screen", Toast.LENGTH_SHORT)
                .show()
            },
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen"
            )
          }
        },
        actions = {
          RestTimerButton()
          WorkoutOptionsDropdownMenu(
            addWorkoutNote = { workoutViewModel.addWorkoutNote() },
            enableWorkoutNameTextfield = { setEditable(!editable) },
            workoutName = workoutName ?: "",
            setWorkoutName = { workoutViewModel.setWorkoutName(it) },
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
        verticalAlignment = Alignment.CenterVertically
      ) {
        WorkoutDuration()
        FinishWorkoutButton(navigateToWorkoutSummaryScreen = { navigateToWorkoutSummaryScreen() })
      }

      ExercisesSetsScreen(
        navController = navController,
      )
    }
  }
}

@Composable
private fun WorkoutDuration(workoutViewModel: WorkoutViewModel = koinViewModel()) {
  val workoutDuration by workoutViewModel.workoutDurationString.collectAsState()
  Text(
    modifier = Modifier.padding(8.dp),
    text = workoutDuration,
    style = MaterialTheme.typography.bodyLarge
  )
}

@Composable
private fun WorkoutNameTextField(
  workoutName: String,
  setWorkoutName: (String) -> Unit,
) {
  var name by remember { mutableStateOf(workoutName) }

  BasicTextField(
    enabled = false,
    modifier = Modifier.padding(PaddingValues(4.dp)),
    textStyle =
    TextStyle(
      fontSize = 5.em,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground
    ),
    value = name,
    onValueChange = {
      name = it
      setWorkoutName(it)
    },
  )
}

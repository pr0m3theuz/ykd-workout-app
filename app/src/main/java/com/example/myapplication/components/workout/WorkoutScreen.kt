package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.FinishWorkoutButton
import com.example.myapplication.components.WorkoutOptionsDropdownMenu
import com.example.myapplication.components.exercise.WorkoutNotes
import com.example.myapplication.components.exerciseDetails.ExerciseDetailsScreen
import com.example.myapplication.components.exercisesList.ExercisesLazyColumn
import com.example.myapplication.components.exercisesList.ExercisesViewModel
import com.example.myapplication.components.setRestTimer.RestTimerButton

// TODO Takes a Workout Object

@Composable
fun WorkoutScreen(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "workoutContent") {
        composable("workoutContent") {
            val viewModel = hiltViewModel<WorkoutViewModel>()
            WorkoutContent(
                navController = navController,
                workoutViewModel = viewModel,
            )
        }
        composable("exercisesList") {
            val viewModel = hiltViewModel<ExercisesViewModel>()
            ExercisesLazyColumn(
                navController = navController,
                exercisesViewModel = viewModel,
            )
        }
        composable(
            "exerciseDetails/{exerciseID}",
            arguments = listOf(navArgument("exerciseID") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val viewModel = hiltViewModel<ExercisesViewModel>()
            navBackStackEntry.arguments?.getString("exerciseID")?.let {
                ExerciseDetailsScreen(
                    navController,
                    exercisePK = it,
                    exercisesViewModel = viewModel,
                )
            }
        }
    }
}

@Composable
fun WorkoutContent(navController: NavController, workoutViewModel: WorkoutViewModel) {
    val workout by workoutViewModel.workout.collectAsState()

    val openFinishWorkoutDialog = remember { mutableStateOf(false) }

    if (openFinishWorkoutDialog.value) {
        FinishWorkoutButton()
        openFinishWorkoutDialog.value = false
    }

    val workoutName by workoutViewModel.workoutName.collectAsState()

    val workoutDuration by workoutViewModel.workoutDurationString.collectAsState()
//    val exercisesSets by workoutViewModel.exercisesSets.collectAsState()

    Scaffold() { _ ->
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WorkoutNameTextField(
                    workoutName,
                    setWorkoutName = { name -> workoutViewModel.setWorkoutName(name) }
                )

                WorkoutOptionsDropdownMenu()
            }

            Text(modifier = Modifier.padding(8.dp), text = workoutDuration)

            Row(
                modifier = Modifier.padding(start = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RestTimerButton()
                FinishWorkoutButton()
            }
            WorkoutNotes(
                placeholder = "Workout Note",
                pinNote = { pin -> workoutViewModel.pinWorkoutNote(pin) },
                notesPinned = workout.notesPinned
            )

            ExercisesSetsScreen(navController = navController)
        }
    }
}

@Composable
private fun WorkoutNameTextField(
    workoutName: String,
    setWorkoutName: (String) -> Unit,
) {
    var name by remember { mutableStateOf(workoutName) }

    BasicTextField(
        enabled = false,
        modifier = Modifier
            .padding(PaddingValues(4.dp)),
        textStyle = TextStyle(
            fontSize = 5.em,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        ),
        value = name,
        onValueChange = {
            name = it
            setWorkoutName(it)
        }
    )
}

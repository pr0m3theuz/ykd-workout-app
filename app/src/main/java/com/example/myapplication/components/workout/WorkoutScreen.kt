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
import androidx.compose.material.icons.outlined.ArrowBack
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
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.BottomNavigationBar
import com.example.myapplication.components.FinishWorkoutButton
import com.example.myapplication.components.WorkoutOptionsDropdownMenu
import com.example.myapplication.components.exercise.WorkoutNotes
import com.example.myapplication.components.setRestTimer.RestTimerButton

// TODO Takes a Workout Object

/*@Composable
fun WorkoutScreen(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "workoutContent") {

    }
}*/

@OptIn(ExperimentalMaterial3Api::class)
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
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    WorkoutNameTextField(
                        workoutName,
                        setWorkoutName = { name -> workoutViewModel.setWorkoutName(name) }
                    )
                    /*                    Text(
                                            text = "Exercises",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            //                    modifier = Modifier.align(Alignment.CenterVertically),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )*/
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            /*TODO Navigate to Previous Screen */
                            navController.navigateUp()
                            Toast.makeText(
                                context,
                                "TODO: Navigate to Previous Screen",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Go to the previous screen"
                        )
                    }
                },
                actions = {
                    RestTimerButton()
                    WorkoutOptionsDropdownMenu()
                },
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
    ) { contentPadding ->
        Column(Modifier.padding(contentPadding)) {
            Row(
                modifier = Modifier.padding(start = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.padding(8.dp), text = workoutDuration)
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

package com.example.myapplication.components.workout

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.myapplication.components.WorkoutViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FinishWorkoutButton(
    navigateToWorkoutSummaryScreen: () -> Unit,
    workoutViewModel: WorkoutViewModel = koinViewModel(),
) {
    val openFinishWorkoutDialog = remember { mutableStateOf(false) }
    val openPartialFinishWorkoutDialog = remember { mutableStateOf(false) }

    fun closePartialFinishWorkoutDialog() {
        openPartialFinishWorkoutDialog.value = false
    }

    fun closeFinishWorkoutDialog() {
        openFinishWorkoutDialog.value = false
    }

    fun openFinishWorkoutDialog() {
        if (workoutViewModel.checkAllSetsComplete()) {
            openFinishWorkoutDialog.value = true
        } else {
            openPartialFinishWorkoutDialog.value = true
        }
    }

    if (openFinishWorkoutDialog.value) {
        FinishWorkoutDialog(
            navigateToWorkoutSummaryScreen = { navigateToWorkoutSummaryScreen() },
            openDialog = { closeFinishWorkoutDialog() },
            confirmFinishWorkout = { workoutViewModel.finishWorkout() }
        )
    }

    if (openPartialFinishWorkoutDialog.value) {
        PartialFinishWorkoutDialog(
            navigateToWorkoutSummaryScreen = { navigateToWorkoutSummaryScreen() },
            openDialog = { closePartialFinishWorkoutDialog() },
            confirmFinishWorkout = { workoutViewModel.finishWorkout() }
        )
    }

    TextButton(
        onClick = { openFinishWorkoutDialog() }
    ) {
        Text(
            text = "FINISH",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun FinishWorkoutDialog(
    navigateToWorkoutSummaryScreen: () -> Unit,
    openDialog: (Boolean) -> Unit,
    confirmFinishWorkout: () -> Unit,
) {
//    TODO Fix Dark Mode
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog(false)
        },
        title = {
            Text(text = "Finish workout?")
        },
        text = { /*Text("Are you sure you want to discard this workout? This cannot be undone.")*/ },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    confirmFinishWorkout()
                    navigateToWorkoutSummaryScreen()
                }
            ) {
                Text("Finish", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                }
            ) {
                Text("Cancel", color = MaterialTheme.colorScheme.tertiary)
            }
        }
    )
}

/* TODO
    "Are you finished?"
    "All invalid/empty sets will be discarded. All sets with valid data will be automatically marked as complete."
    "Finish Workout"
*/

@Composable
fun PartialFinishWorkoutDialog(
    navigateToWorkoutSummaryScreen: () -> Unit,
    openDialog: (Boolean) -> Unit,
    confirmFinishWorkout: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog(false)
        },
        title = {
            Text(text = "Are you finished?", color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            Text(
                color = MaterialTheme.colorScheme.onSurface, text =
                "All invalid/empty sets will be discarded. " +
                        " All sets with valid data will be automatically marked as complete."
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    confirmFinishWorkout()
                    navigateToWorkoutSummaryScreen()
                }
            ) {
                Text("Finish Workout", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                }
            ) {
                Text("Cancel", color = MaterialTheme.colorScheme.tertiary)
            }
        }
    )
}

package com.example.myapplication.components

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel

@Composable
fun FinishWorkoutButton(
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val openFinishWorkoutDialog = remember { mutableStateOf(false) }
    val openPartialFinishWorkoutDialog = remember { mutableStateOf(false) }

    fun closePartialFinishWorkoutDialog() {
        openPartialFinishWorkoutDialog.value = false
    }

    fun closeFinishWorkoutDialog() {
        openFinishWorkoutDialog.value = false
    }

    Log.i(
        "workoutViewModel.checkAllSetsComplete()",
        workoutViewModel.checkAllSetsComplete().toString()
    )

    fun openFinishWorkoutDialog() {
        if (workoutViewModel.checkAllSetsComplete()) {
            openFinishWorkoutDialog.value = true
        } else {
            openPartialFinishWorkoutDialog.value = true
        }
    }

    if (openFinishWorkoutDialog.value) {
        FinishWorkoutDialog(
            openDialog = { closeFinishWorkoutDialog() },
            confirmFinishWorkout = { /* TODO cancel -> (cancel) */ }
        )
    }

    if (openPartialFinishWorkoutDialog.value) {
        PartialFinishWorkoutDialog(
            openDialog = { closePartialFinishWorkoutDialog() },
            confirmFinishWorkout = { /* TODO cancel -> (cancel) */ }
        )
    }
    Log.i("openPartialFinishWorkoutDialog.value", openPartialFinishWorkoutDialog.value.toString())

    TextButton(
        onClick = { openFinishWorkoutDialog() }
    ) {
        Text(
            text = "FINISH",
            fontSize = 16.sp
        )
    }
}

@Composable
fun FinishWorkoutDialog(
    openDialog: (Boolean) -> Unit,
    confirmFinishWorkout: (Boolean) -> Unit
) {
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
                    confirmFinishWorkout(true)
                    // TODO Save workout
                }
            ) {
                Text("Finish")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                }
            ) {
                Text("Cancel")
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
    openDialog: (Boolean) -> Unit,
    confirmFinishWorkout: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog(false)
        },
        title = {
            Text(text = "Are you finished?")
        },
        text = { Text("All invalid/empty sets will be discarded. All sets with valid data will be automatically marked as complete.") },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    confirmFinishWorkout(true)
                    // TODO Save workout
                }
            ) {
                Text("Finish Workout")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

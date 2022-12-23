package com.example.myapplication.components

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable

// TODO Complete this Feature

@Composable
fun ReplaceExerciseDialog(
    openDialog: (Boolean) -> Unit
//    confirmReplaceExercise: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog(false)
        },
        title = {
            Text(text = "Replace Exercise?")
        },
        text = { Text("The set that you've already added to this exercise will be replaced.") },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
//                    TODO Create Replace Exercise Dialog
//                    confirmReplaceExercise(true)
                }
            ) {
                Text("Replace")
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

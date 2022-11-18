package com.example.myapplication.components

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable

@Composable
fun ConfirmCompletedSetDeletionDialog(
    openDialog: (Boolean) -> Unit,
    confirmSetDelete: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog(false)
        },
        title = {
            Text(text = "Confirm Deleting Set")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    confirmSetDelete(true)
                }
            ) {
                Text("Delete Set")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    confirmSetDelete(false)
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

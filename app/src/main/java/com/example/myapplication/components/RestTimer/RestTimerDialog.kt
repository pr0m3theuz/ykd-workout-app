package com.example.myapplication.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.components.RestTimer.GoogleClockTimer

// @Preview
@Composable
fun RestTimerScreen(
    openDialog: (Boolean) -> Unit
) {
    RestTimerDialog(
        { close -> openDialog(close) }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RestTimerDialog(
    openDialog: (Boolean) -> Unit
) {
    AlertDialog(
        modifier = Modifier.fillMaxSize(),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        shape = RoundedCornerShape(0),
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            openDialog(false)
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { openDialog(false) }
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Localized description",
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(text = "Timer", fontSize = 28.sp, modifier = Modifier.padding(0.dp))
            }
        },
        text = {
            Column {
                GoogleClockTimer()
            }
        },
        confirmButton = {
            /* TextButton(
                 onClick = {
                     openDialog(false)
                     run { insertWarmUpSets(warmUpSets) }
                 }
             ) {
                 Text("Insert")
             }*/
        },
        dismissButton = {
/*            TextButton(
                onClick = {
                    openDialog(false)
                }
            ) {
                Text("Cancel")
            }*/
        }
    )
}

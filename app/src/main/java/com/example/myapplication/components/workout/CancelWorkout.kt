package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.myapplication.components.DiscardWorkoutDialog

@Composable
fun CancelWorkout(cancelWorkout: () -> Unit) {
    val openDiscardWorkoutDialog = remember { mutableStateOf(false) }

    fun closeDiscardWorkoutDialog() {
        openDiscardWorkoutDialog.value = false
    }

    if (openDiscardWorkoutDialog.value) {
        DiscardWorkoutDialog(
            openDialog = { closeDiscardWorkoutDialog() },
            cancelWorkout = { cancelWorkout() }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { openDiscardWorkoutDialog.value = true }) {
            Text("Cancel Workout", color = Color.Red)
        }
    }
}

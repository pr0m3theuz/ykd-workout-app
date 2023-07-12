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
import com.example.myapplication.components.addExercisesList.AddExercisesScreen

@Composable
fun AddExercise() {
    val (openDialog, setOpenDialog) = remember { mutableStateOf(false) }

    if (openDialog) {
        AddExercisesScreen(setOpenDialog = setOpenDialog)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = {
            /*TODO Do something!*/
            setOpenDialog(true)
        }) {
            Text("Add Exercise")
        }
    }
}

// TODO Create Add Exercise Dialog

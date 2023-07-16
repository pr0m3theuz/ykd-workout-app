package com.example.myapplication.components.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AddSet(addSet: () -> Unit) {
    //    addSet(exerciseSets)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = { addSet() },
            modifier = Modifier.weight(1.0f, fill = true),
            shape = MaterialTheme.shapes.extraSmall,
        ) {
            Text("Add Set")
        }
    }
}

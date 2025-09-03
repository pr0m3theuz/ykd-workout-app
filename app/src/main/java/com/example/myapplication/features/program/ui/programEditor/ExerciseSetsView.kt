package com.example.myapplication.features.program.ui.programEditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExerciseSetsView(exerciseItem: ExerciseWorkoutItem, editMode: Boolean) {
  // TODO Add % of e1RM Intensity
  // TODO Add set type
  // TODO Replace with SetContent

  Column(modifier = Modifier.Companion.padding(16.dp)) {
    Card(
      modifier = Modifier.Companion.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
      Column(Modifier.Companion.padding(16.dp)) {
        Text(
          exerciseItem.exercise.name,
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
          exerciseItem.exercise.targetMuscle,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
      }
    }
    Spacer(Modifier.Companion.height(16.dp))
    Row(
      modifier = Modifier.Companion
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Companion.CenterVertically
    ) {
      Text("Sets", style = MaterialTheme.typography.titleLarge)
      if (editMode) {
        Button(onClick = { /* TODO: Add Set */ }) {
          Icon(
            Icons.Filled.Add,
            contentDescription = "Add Set",
            modifier = Modifier.Companion.size(18.dp)
          )
          Spacer(Modifier.Companion.width(4.dp))
          Text("Add Set")
        }
        // TODO ExerciseOptionsDropdownMenu()
      }
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(exerciseItem.sets.withIndex().toList(), key = { (_, set) -> set.id }) { (index, set) ->
        Card(
          modifier = Modifier.Companion.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
//          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(Modifier.Companion.padding(16.dp)) {
            Row(
              modifier = Modifier.Companion.fillMaxWidth(),
              verticalAlignment = Alignment.Companion.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                if (editMode) {
                  Icon(
                    Icons.Filled.DragHandle,
                    contentDescription = "Drag",
                    modifier = Modifier.Companion
                      .size(18.dp)
                      .padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                Text("Set ${index + 1}", style = MaterialTheme.typography.titleMedium)
              }

              if (editMode) {
                Row {
                  IconButton(
                    onClick = { /* TODO: Edit Set */ },
                    modifier = Modifier.Companion.size(36.dp)
                  ) {
                    Icon(Icons.Filled.Edit, "Edit Set", modifier = Modifier.Companion.size(18.dp))
                  }
                  IconButton(
                    onClick = { /* TODO: Delete Set */ },
                    modifier = Modifier.Companion.size(36.dp)
                  ) {
                    Icon(
                      Icons.Filled.Delete,
                      "Delete Set",
                      tint = MaterialTheme.colorScheme.error,
                      modifier = Modifier.Companion.size(18.dp)
                    )
                  }
                }
              }
            }
            Spacer(Modifier.Companion.height(8.dp))
            Row(
              Modifier.Companion.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              OutlinedTextField(
                modifier = Modifier.Companion.weight(1f),
                value = set.targetReps,
                onValueChange = {},
                label = { Text("Min Reps", style = MaterialTheme.typography.labelMedium) },
              )
              OutlinedTextField(
                modifier = Modifier.Companion.weight(1f),
                value = set.targetReps,
                onValueChange = {},
                label = { Text("Max Reps", style = MaterialTheme.typography.labelMedium) },
              )
              OutlinedTextField(
                modifier = Modifier.Companion.weight(1f),
                value = set.targetRIR.toString(),
                onValueChange = {},
                label = { Text("Target RIR", style = MaterialTheme.typography.labelMedium) },
              )

            }
          }
        }
      }
    }
  }
}
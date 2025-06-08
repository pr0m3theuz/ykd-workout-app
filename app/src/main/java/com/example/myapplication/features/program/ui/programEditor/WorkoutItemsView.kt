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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WorkoutItemsView(workout: Workout, editMode: Boolean, navigateTo: (EditorView, Any?) -> Unit) {
  Column(modifier = Modifier.Companion.padding(16.dp)) {
    Row(
      modifier = Modifier.Companion
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Companion.CenterVertically
    ) {
      Text("Exercises", style = MaterialTheme.typography.titleLarge)
      if (editMode) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(onClick = { /* TODO: Add Exercise */ }) {
            Icon(
              Icons.Filled.Add,
              contentDescription = "Add Exercise",
              modifier = Modifier.Companion.size(18.dp)
            )
            Spacer(Modifier.Companion.width(4.dp))
            Text("Exercise")
          }
          Button(
            onClick = { /* TODO: Add Superset */ },
//            colors = ButtonDefaults.buttonColors()
          ) {
            Icon(
              Icons.Filled.Add,
              contentDescription = "Add Exercise",
              modifier = Modifier.Companion.size(18.dp)
            )
            Spacer(Modifier.Companion.width(4.dp))
            Text("Superset")
          }
        }
      }
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      items(workout.workoutItems, key = { it.id }) { item ->
        when (item) {
          is ExerciseWorkoutItem -> {
            ListItemCard(
              onClick = { navigateTo(EditorView.EXERCISE_SETS, item) },
              editMode = editMode,
              onEdit = { /* TODO: Edit exercise item */ },
              onDelete = { /* TODO: Delete exercise item */ }
            ) {
              Column(modifier = Modifier.Companion.weight(1f)) {
                Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                  if (editMode) {
                    Icon(
                      Icons.Filled.DragHandle,
                      contentDescription = "Drag",
                      modifier = Modifier.Companion
//                        .size(24.dp)
                        .padding(end = 4.dp),
                      tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  Text(item.exercise.name, style = MaterialTheme.typography.titleMedium)
                }
                Text(
                  item.exercise.targetMuscle,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  "${item.sets.size} sets • ${item.sets.firstOrNull()?.targetReps ?: ""} reps",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          is SupersetWorkoutItem -> {
            Card(
              modifier = Modifier.Companion.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
//              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.Companion.padding(16.dp)) {
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
                          .size(32.dp)
                          .padding(end = 4.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                    Text("${item.name} (Superset)", style = MaterialTheme.typography.titleMedium)
                  }
                  if (editMode) {
                    Row {
                      IconButton(onClick = { /* TODO: Edit Superset */ }) {
                        Icon(
                          Icons.Filled.Edit,
                          "Edit Superset"
                        )
                      }
                      IconButton(onClick = { /* TODO: Delete Superset */ }) {
                        Icon(
                          Icons.Filled.Delete,
                          "Delete Superset",
                          tint = MaterialTheme.colorScheme.error
                        )
                      }
                    }
                  }
                }
                Spacer(Modifier.Companion.height(8.dp))
                Column(
                  modifier = Modifier.Companion
                    .padding(start = 8.dp)
                    .fillMaxWidth()
                    .padding(start = 12.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  item.exercises.forEach { ex ->
                    Column {
                      Text(ex.exercise.name, style = MaterialTheme.typography.bodyLarge)
                      Text(
                        "${ex.sets.size} sets • ${ex.sets.firstOrNull()?.targetReps ?: ""} reps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
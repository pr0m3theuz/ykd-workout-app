package com.example.myapplication.features.program.ui.programEditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MicrocyclesView(
  mesocycle: Mesocycle,
  editMode: Boolean,
  navigateTo: (EditorView, Any?) -> Unit
) {
  Column(modifier = Modifier.padding(16.dp)) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Training Weeks", style = MaterialTheme.typography.titleLarge)
      if (editMode) {
        Button(onClick = { /* TODO: Add Week */ }) {
          Icon(Icons.Filled.Add, contentDescription = "Add Week", modifier = Modifier.size(18.dp))
          Spacer(Modifier.width(4.dp))
          Text("Add Week")
        }
      }
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(mesocycle.microcycles, key = { it.id }) { microcycle ->
        ListItemCard(
          onClick = { navigateTo(EditorView.WORKOUTS, microcycle) },
          editMode = editMode,
          onEdit = { /* TODO: Edit microcycle */ },
          onDelete = { /* TODO: Delete microcycle */ }
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (editMode) {
                Icon(
                  Icons.Filled.DragHandle,
                  contentDescription = "Drag",
                  modifier = Modifier
                    .size(18.dp)
                    .padding(end = 4.dp),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Text(microcycle.name, style = MaterialTheme.typography.titleMedium)
            }
            Text(
              "${microcycle.workouts.size} workouts",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}


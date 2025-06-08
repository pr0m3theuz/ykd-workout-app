package com.example.myapplication.features.program.ui.programEditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.ui.theme.IBMColorBlindPalette


@Composable
fun ProgramView(program: Program, editMode: Boolean, navigateTo: (EditorView, Any?) -> Unit) {
  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(
            Brush.horizontalGradient(
              colors = listOf(IBMColorBlindPalette.colors[0], IBMColorBlindPalette.colors[2])
            )
          )
          .padding(16.dp)
      ) {
        Column {
          Text(
            program.name,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary
          )
          Spacer(Modifier.height(8.dp))
          Text(
            program.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
          )
          Spacer(Modifier.height(16.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Filled.CalendarToday,
                contentDescription = "Phases",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(Modifier.width(4.dp))
              Text(
                "${program.mesocycles.size} phases",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
              )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Filled.FitnessCenter,
                contentDescription = "Goal",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
              ) // Changed from Target
              Spacer(Modifier.width(4.dp))
              Text(
                "Muscle Building",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
              )
            }
          }
        }
      }
    }
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Program Structure", style = MaterialTheme.typography.titleLarge)
        if (editMode) {
          TextButton(onClick = { /* TODO: Edit Program */ }) {
            Text("Edit Program")
          }
        }
      }
    }
    item {
      Card(
        onClick = { navigateTo(EditorView.MESOCYCLES, null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Mesocycles", style = MaterialTheme.typography.titleMedium)
            Text(
              "Training phases and periodization",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(
              program.mesocycles.size.toString(),
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              "phases",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

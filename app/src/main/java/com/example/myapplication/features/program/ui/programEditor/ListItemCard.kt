package com.example.myapplication.features.program.ui.programEditor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListItemCard(
  onClick: () -> Unit,
  editMode: Boolean,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  showPlayButton: Boolean = false,
  onPlay: () -> Unit = {},
  content: @Composable RowScope.() -> Unit
) {
  Card(
    onClick = onClick,
    modifier = Modifier.Companion.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
//    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier.Companion
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.Companion.CenterVertically
    ) {
      content() // Composable content for the main part of the list item
      Spacer(Modifier.Companion.width(8.dp))
      if (showPlayButton && !editMode) {
        IconButton(
          onClick = onPlay,
          colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
          Icon(Icons.Filled.PlayArrow, contentDescription = "Play")
        }
      }
      if (editMode) {
        Row {
          IconButton(onClick = onEdit) {
            Icon(Icons.Filled.Edit, "Edit", modifier = Modifier.Companion.size(20.dp))
          }
          IconButton(onClick = onDelete) {
            Icon(
              Icons.Filled.Delete,
              "Delete",
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.Companion.size(20.dp)
            )
          }
        }
      }
    }
  }
}
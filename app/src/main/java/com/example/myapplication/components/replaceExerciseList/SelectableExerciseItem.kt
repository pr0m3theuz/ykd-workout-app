package com.example.myapplication.components.addExercisesList

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.data.ExerciseModel

@Composable
fun SelectableExerciseItem(
  exercise: ExerciseModel,
  selectedExercise: Boolean,
  muscleGroupName: String?,
  selectExercise: (_exercise: ExerciseModel, selected: Boolean) -> Unit,
  workoutViewModel: WorkoutViewModel = hiltViewModel()
) {

  val padding = 8.dp

  val selected by remember { mutableStateOf(selectedExercise) }

  val colors =
    when (isSystemInDarkTheme()) {
      true -> ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
      else -> ListItemDefaults.colors(containerColor = Color.Transparent)
    }

  ListItem(
    colors = colors,
    modifier =
    Modifier /*.padding(vertical = 8.dp)*/
      .fillMaxWidth()
      .selectable(
        enabled = true,
        role = Role.RadioButton,
        selected = selectedExercise,
        onClick = {
          selectExercise(exercise, !selectedExercise)
        }
      ),
    headlineContent = {
      Text(
        text = exercise.name,
        //        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
      )
    },
    supportingContent = {
      Text(
        text = muscleGroupName ?: "-",
        //        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onBackground
      )
    },
    trailingContent = {},
    leadingContent = {
      if (selectedExercise) {
        Icon(
          imageVector = Icons.Filled.CheckCircle,
          tint = Color.Green,
          contentDescription = "Selected"
        )
      } else {
        Icon(
          imageVector = Icons.Outlined.Error,
          tint = MaterialTheme.colorScheme.error,
          contentDescription = "No exercise image available."
        )
      }
    }
  )

  Divider()

  /*  Row(
    modifier =
      Modifier.padding(vertical = 8.dp)
        .fillMaxWidth()
        .toggleable(
          enabled = true,
          value = selected,
          onValueChange = {
            onSelected(it)
            selectExercise(exercise, it)
          }
        ),
    //        horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Spacer(modifier = Modifier.width(padding))
    Box(
      modifier =
        Modifier.size(width = 50.dp, height = 50.dp)
          .background(
            if (selected) {
              MaterialTheme.colorScheme.surface
            } else {
              MaterialTheme.colorScheme.errorContainer
            }
          )
    ) {
      //            TODO Get Image from YouTube Thumbnail
      if (selected) {
        Icon(
          modifier = Modifier.align(Alignment.Center).size(40.dp),
          imageVector = Icons.Filled.CheckCircle,
          tint = Color.Green,
          contentDescription = "Selected"
        )
      } else {
        Icon(
          modifier = Modifier.align(Alignment.Center).size(40.dp),
          imageVector = Icons.Outlined.Error,
          tint = MaterialTheme.colorScheme.error,
          contentDescription = "No exercise image available."
        )
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

    Column {
      Text(
        text = exercise.name,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = muscleGroupName ?: "-",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Box(modifier = Modifier.width(padding))
  }*/
}

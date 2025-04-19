package com.example.myapplication.features.workout.feature.addExercisesList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.common.domain.model.Exercise

@Composable
fun ToggleableExerciseItem(
  exercise: Exercise,
  muscleGroupName: String?,
  navController: NavController,
  selectExercise: (_exercise: Exercise, selected: Boolean) -> Unit,
  navigateToDetailPane: (exerciseID: String) -> Unit,
) {

  val padding = 8.dp

  val (selected, onSelected) = remember { mutableStateOf(false) }

  val colors =
    when (isSystemInDarkTheme()) {
      true -> ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
      else -> ListItemDefaults.colors(containerColor = Color.Transparent)
    }

  ListItem(
    colors = colors,
    modifier = Modifier /*.padding(vertical = 8.dp)*/.fillMaxWidth(),
    headlineContent = {
      Text(
        modifier =
          Modifier.clickable(
            enabled = true,
            onClickLabel = "See more details about ${exercise.name} ",
            onClick = {
              // navController.navigate("exerciseDetails/${exercise._id.toHexString()}")
              navigateToDetailPane(exercise.id.toHexString())
            },
          ),
        text = exercise.name,
        //        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
      )
    },
    supportingContent = {
      Text(
        text = muscleGroupName ?: "-",
        //        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onBackground,
      )
    },
    trailingContent = {},
    leadingContent = {
      IconToggleButton(
        modifier = Modifier.size(40.dp),
        checked = selected,
        onCheckedChange = {
          onSelected(it)
          selectExercise(exercise, it)
        },
        enabled = true,
      ) {
        Icon(
          modifier = Modifier.size(36.dp),
          imageVector = if (selected) Icons.Filled.CheckCircle else Icons.Outlined.Error,
          tint =
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
          contentDescription = if (selected) "Selected" else "No exercise image available.",
        )
      }
    },
  )

  HorizontalDivider()
}

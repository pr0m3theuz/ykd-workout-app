package com.example.myapplication.components.addExercisesList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.myapplication.data.model.Exercise

@Composable
fun ToggleableExerciseItem(
  exercise: Exercise,
  muscleGroupName: String?,
  selectExercise: (_exercise: Exercise, selected: Boolean) -> Unit,
  navController: NavController
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
    modifier = Modifier /*.padding(vertical = 8.dp)*/.fillMaxWidth()
    /*.toggleable(
      enabled = true,
      role = Role.Checkbox,
      value = selected,
      onValueChange = {
        onSelected(it)
        selectExercise(exercise, it)
      }
    )*/,
    headlineContent = {
      Text(
        modifier = Modifier.clickable(
          enabled = true,
          onClickLabel = "See more details about ${exercise.name} ",
          onClick = { navController.navigate("exerciseDetails/${exercise._id.toHexString()}") }
        ),
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
      IconToggleButton(
        checked = selected,
        onCheckedChange = {
          onSelected(it)
          selectExercise(exercise, it)
        },
        enabled = true,
      ) {
        Icon(
          imageVector = if (selected) Icons.Filled.CheckCircle else Icons.Outlined.Error,
          tint =
          if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
          contentDescription = if (selected) "Selected" else "No exercise image available."
        )
      }
    }
  )

  HorizontalDivider()

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

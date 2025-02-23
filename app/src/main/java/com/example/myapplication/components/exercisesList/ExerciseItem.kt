package com.example.myapplication.components.exercisesList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Exercise

@Composable
fun ExerciseItem(
  exercise: Exercise,
  muscleGroupName: String?,
  navController: NavController,
  navigateToDetailPane: (exerciseID: String) -> Unit,
) {

  val padding = 8.dp

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
      .clickable(
        enabled = true,
        onClickLabel = "See more details about ${exercise.name} ",
        onClick = {
//            navController.navigate("exerciseDetails/${exercise._id.toHexString()}")
          navigateToDetailPane(exercise.id.toHexString())
        },
      ),
    headlineContent = {
      Text(
        text = exercise.name,
        //        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
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
      // TODO Get Image from YouTube Thumbnail
      Icon(
        //        modifier = Modifier.size(40.dp),
        imageVector = Icons.Outlined.Error,
        tint = MaterialTheme.colorScheme.error,
        contentDescription = "No exercise image available.",
      )
    },
  )

  HorizontalDivider()
}

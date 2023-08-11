package com.example.myapplication.components.exercisesList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Divider
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
fun ExerciseItem(exercise: Exercise, muscleGroupName: String?, navController: NavController) {

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
            onClick = { navController.navigate("exerciseDetails/${exercise._id}") }
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
      // TODO Get Image from YouTube Thumbnail
      Icon(
        //        modifier = Modifier.size(40.dp),
        imageVector = Icons.Outlined.Error,
        tint = MaterialTheme.colorScheme.error,
        contentDescription = "No exercise image available."
      )
    }
  )

  Divider()

  /*Row(
    modifier =
      Modifier.padding(vertical = 8.dp)
        .fillMaxWidth()
        .clickable(
          enabled = true,
          onClickLabel = null,
          onClick = { navController.navigate("exerciseDetails/${exercise.exerciseID}") }
        ), // TODO Create Exercise Details Screen
    //        horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Spacer(modifier = Modifier.width(padding))
    Box(
      modifier =
        Modifier.size(width = 50.dp, height = 50.dp)
          .background(MaterialTheme.colorScheme.errorContainer)
    ) {
      //            TODO Get Image from YouTube Thumbnail
      Icon(
        modifier = Modifier.align(Alignment.Center).size(40.dp),
        imageVector = Icons.Outlined.Error,
        tint = MaterialTheme.colorScheme.error,
        contentDescription = "No exercise image available."
      )
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
